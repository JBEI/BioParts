package org.abf.bps.lib.search.blast;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.search.BlastQuery;
import org.abf.bps.lib.dto.search.SearchResult;
import org.abf.bps.lib.index.SearchIndex;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class BlastPlus {

    private final Path blastDbFolder;

    private static final String BLAST_FOLDER = "blast";                 // folder in the data directory for blast
    private static final String BLAST_DB_NAME = "bps";                  // name of the blast database
    private static final String BLAST_DB_FOLDER = "db";                 // folder for creating blast database

    public BlastPlus() throws IOException {
        blastDbFolder = Paths.get(Constants.DATA_DIR, BLAST_FOLDER, BLAST_DB_FOLDER);
        Logger.info("Using blast index folder at: " + blastDbFolder.getParent().toString());

        if (!Files.exists(blastDbFolder)) {
            Files.createDirectories(blastDbFolder);
        }
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
        BlastSearch blastSearch = new BlastSearch(this.blastDbFolder, BLAST_DB_NAME);
        return blastSearch.run(query, options);
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
}
