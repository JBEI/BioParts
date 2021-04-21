package org.abf.bps.lib.search.blast;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.abf.bps.lib.dto.entry.PartData;
import org.abf.bps.lib.dto.search.BlastProgram;
import org.abf.bps.lib.dto.search.BlastQuery;
import org.abf.bps.lib.dto.search.SearchResult;
import org.abf.bps.lib.index.SearchIndex;
import org.abf.bps.lib.utils.Utils;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Enables (command line) interaction with BLAST+
 * <p>
 * Current usage is for blast searches and auto-annotation support
 *
 * @author Hector Plahar
 */
public class BlastPlus implements Closeable {

    private final BufferedWriter writer;
    private final Path blastDbFolder;

    private static final String BLAST_FOLDER = "blast";                 // folder in the data directory for blast
    private static final String BLAST_DB_NAME = "bps";                  // name of the blast database
    private static final String BLAST_DB_FOLDER = "db";                 // folder for creating blast database
    private static final String BLAST_FASTA_FILE = "blastfastafile.fa"; // name of fasta file for blast database

    // /data_dir/blast/{db/bps*|blastfastafile.fa}
    public BlastPlus() throws IOException {
        blastDbFolder = Paths.get(Constants.DATA_DIR, BLAST_FOLDER, BLAST_DB_FOLDER);
        Logger.info("Using blast index folder at: " + blastDbFolder.getParent().toString());

        if (!Files.exists(blastDbFolder)) {
            Files.createDirectories(blastDbFolder);
        }

        // check fasta file used to create blast database
        writer = Files.newBufferedWriter(Paths.get(blastDbFolder.getParent().toString(), BLAST_FASTA_FILE), Charset.defaultCharset(),
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Runs a blast query in the specified database folder
     * using the specified options
     *
     * @param query   wrapper around blast query including options such as blast type
     * @param options command line options for blast
     * @return results of the query run. An empty string is returned if the specified blast database does not exist
     * in the ice data directory
     * @throws BlastException on exception running blast on the command line
     */
    private String runBlastQuery(BlastQuery query, String... options) throws BlastException {
        if (query.getBlastProgram() == null)
            query.setBlastProgram(BlastProgram.BLAST_N);

        try {
            Path blastDbFile = Paths.get(this.blastDbFolder.toString(), (BLAST_DB_NAME + ".nsq"));
            if (!Files.exists(blastDbFile)) {
                return "";
            }

            String[] blastCommand = new String[3 + options.length];
            Path commandPath = Paths.get(Constants.BLAST_INSTALL, query.getBlastProgram().getName());

            blastCommand[0] = commandPath.toString();
            blastCommand[1] = "-db";
            blastCommand[2] = blastDbFile.getParent().toString();
            System.arraycopy(options, 0, blastCommand, 3, options.length);

            Process process = Runtime.getRuntime().exec(blastCommand);
            ProcessResultReader reader = new ProcessResultReader(process.getInputStream());
            reader.start();
            BufferedWriter programInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            programInputWriter.write(query.getSequence());
            programInputWriter.flush();
            programInputWriter.close();
            process.getOutputStream().close();

            // TODO this should go into the thread itself & have future wait on it
            final int exitValue = process.waitFor();
            switch (exitValue) {
                case 0:
                    return reader.toString();

                case 1:
                    Logger.error("Error in query sequence(s) or BLAST options");
                    break;

                case 2:
                    Logger.error("Error in BLAST database");
                    break;

                default:
                    Logger.error("Unknown exit value " + exitValue);
            }
            return null;
        } catch (Exception e) {
            Logger.error(e);
            throw new BlastException(e);
        }
    }

    /**
     * Run a blast query using the following output format options
     * <ul>
     * <li><code>stitle</code> - subject title</li>
     * <li><code>qstart</code> - query match start index</li>
     * <li><code>qend</code> - query match end index</li>
     * <li><code>sstart</code> - subject match start index</li>
     * <li><code>send</code></li>
     * <li><code>sstrand</code></li>
     * <li><code>evalue</code></li>
     * <li><code>bitscore</code></li>
     * <li><code>length</code> - alignment length</li>
     * <li><code>nident</code> - number of identical matches</li>
     * </ul>
     *
     * @param query wrapper around blast query
     * @return map of unique entry identifier (whose sequence was a subject) to the search result hit details
     * @throws BlastException on exception running blast
     */
    public HashMap<String, SearchResult> runBlast(BlastQuery query) throws BlastException {
        String result = runBlastQuery(query, "-perc_identity", "95", "-outfmt",
            "10 stitle qstart qend sstart send sstrand evalue bitscore score length nident");
        if (result == null)
            throw new BlastException("Exception running blast");
        return processBlastOutput(result, query.getSequence().length());
    }

    /**
     * Parses a blast output that represents a single hit
     *
     * @param line blast output for hit
     * @return object wrapper around details of the hit
     */
    private static SearchResult parseBlastOutputLine(String[] line) {
        try {
            String recordId = line[0];
            SearchIndex index = new SearchIndex();
            SearchResult searchResult = index.getByRecordId(recordId);
            if (searchResult == null)
                return null;

            searchResult.seteValue(line[6]);
            searchResult.setScore(Float.parseFloat(line[8].trim()));
            searchResult.setAlignment(line[10]);
            searchResult.setQueryLength(Integer.parseInt(line[9].trim()));
            searchResult.setNident(Integer.parseInt(line[10].trim()));
            return searchResult;
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }

    /**
     * Processes the result of a blast search
     *
     * @param blastOutput result output from running blast on the command line
     * @param queryLength length of query sequence
     * @return mapping of entryId to search result object containing information about the blast search for that particular hit
     */
    private static LinkedHashMap<String, SearchResult> processBlastOutput(String blastOutput, int queryLength) {
        LinkedHashMap<String, SearchResult> hashMap = new LinkedHashMap<>();

        try (CSVReader reader = new CSVReader(new StringReader(blastOutput))) {
            List<String[]> lines = reader.readAll();
            reader.close();

            for (String[] line : lines) {
                SearchResult info = parseBlastOutputLine(line);
                if (info == null)
                    continue;

                info.setQueryLength(queryLength);
                String idString = info.getEntryInfo().getRecordId();
                // if there is an existing record for same entry with a lower relative score then replace
                hashMap.putIfAbsent(idString, info);
            }
        } catch (IOException | CsvException e) {
            Logger.error(e);
            return null;
        }

        return hashMap;
    }

    /**
     * Generates (and runs) command line commands for creating the blast database
     *
     * @throws IOException on exception getting the results of the command line run
     */
    private void formatBlastDb() throws IOException {
        ArrayList<String> commands = new ArrayList<>();

        // create command line command
        commands.add(Paths.get(Constants.BLAST_INSTALL, "makeblastdb").toString());
        commands.add("-dbtype nucl");
        commands.add("-in");
        commands.add(Paths.get(blastDbFolder.getParent().toString(), BLAST_FASTA_FILE).toString());

        commands.add("-logfile");
        commands.add(Paths.get(blastDbFolder.toString(), (BLAST_DB_NAME + ".log")).toString());

        commands.add("-out");
        commands.add(Paths.get(blastDbFolder.toString(), BLAST_DB_NAME).toString());

        String commandString = Utils.join(" ", commands);
        Logger.info("makeblastdb: " + commandString);

        // run command
        Runtime runTime = Runtime.getRuntime();
        Path blastDbDir = Paths.get(Constants.BLAST_INSTALL);
        Process process = runTime.exec(commandString, new String[0], blastDbDir.toFile());

        // wait for and get results of run
        InputStream blastOutputStream = process.getInputStream();
        InputStream blastErrorStream = process.getErrorStream();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        String outputString = Utils.getString(blastOutputStream);
        blastOutputStream.close();
        Logger.debug("format output was: " + outputString);

        String errorString = Utils.getString(blastErrorStream);
        blastErrorStream.close();
        Logger.debug("format error was: " + errorString);
        process.destroy();

        if (errorString.length() > 0) {
            Logger.error(errorString);
            throw new IOException("Could not make blast db");
        }
    }

    /**
     * Write the sequence file for the associated part data to fasta file for blast
     *
     * @param partData part data
     * @param sequence sequence data
     * @throws IOException on exception writing fasta sequence
     */
    public void writeSequenceToFasta(PartData partData, FeaturedDNASequence sequence) throws IOException {
        if (partData == null || sequence == null || sequence.getSequence() == null || sequence.getSequence().isEmpty())
            return;

        // get sequence from
        String sequenceString;
        SymbolList symL;
        try {
            symL = DNATools.createDNA(sequence.getSequence().trim());
            sequenceString = breakUpLines(symL.seqString() + symL.seqString());
        } catch (IllegalSymbolException e1) {
            Logger.error(e1);
            return;
        }

        sequenceString = sequenceString.replaceAll("\n", "");
        if (sequenceString.length() > 0) {
            writer.write(">" + partData.getRecordId() + "\n");
            writer.write(sequenceString + "\n");
            writer.flush();
        }
    }

    /**
     * Format into 6 column, 10 basepairs per column display.
     *
     * @param input sequence string.
     * @return Formatted sequence output.
     */
    public static String breakUpLines(String input) {
        StringBuilder result = new StringBuilder();

        int counter = 0;
        int index = 0;
        int end = input.length();
        while (index < end) {
            result.append(input.charAt(index));
            counter = counter + 1;
            index = index + 1;

            if (counter == 59) {
                result.append("\n");
                counter = 0;
            }
        }
        return result.toString();
    }

    @Override
    public void close() throws IOException {
        writer.close();
        formatBlastDb();
    }

    /**
     * Thread that reads the result of a command line process execution
     */
    static class ProcessResultReader extends Thread {
        final InputStream inputStream;
        final StringBuilder sb;

        ProcessResultReader(final InputStream is) {
            this.inputStream = is;
            this.sb = new StringBuilder();
        }

        public void run() {
            try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                final BufferedReader br = new BufferedReader(inputStreamReader);
                String line;
                while ((line = br.readLine()) != null) {
                    this.sb.append(line).append("\n");
                }
            } catch (final IOException ioe) {
                Logger.error(ioe.getMessage());
                throw new RuntimeException(ioe);
            }
        }

        @Override
        public String toString() {
            return this.sb.toString();
        }
    }
}
