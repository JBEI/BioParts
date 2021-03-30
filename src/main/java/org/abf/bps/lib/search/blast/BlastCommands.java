package org.abf.bps.lib.search.blast;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Commands for running blast
 *
 * @author Hector Plahar
 */
public class BlastCommands {

    private final BlastCommandType type;
    private final Path dbFolder;
    private final String blastDbName;
    private final Path fastaFile;

    /**
     * @param type     type of blast command to run
     * @param dbFolder location of the database folder to run the command in. If a fasta file is required
     *                 it is expected to be one level up with the extension ".fa" or ".fasta"
     */
    public BlastCommands(BlastCommandType type, Path dbFolder, String blastDbName, Path fastaFile) {
        this.type = type;
        this.dbFolder = dbFolder;
        this.blastDbName = blastDbName;
        this.fastaFile = fastaFile;
    }

    public void execute() throws IOException {
        switch (type) {
            case MAKE_BLAST_DB:
                formatBlastDb();
                return;
        }
    }

    /**
     * Generates (and runs) command line commands for creating the blast database
     *
     * @throws IOException on exception getting the results of the command line run
     */
    private void formatBlastDb() throws IOException {
        List<String> commands = new ArrayList<>();

        // create command line command
        commands.add(Paths.get(Constants.BLAST_INSTALL, "makeblastdb").toString());
        commands.add("-dbtype nucl");
        commands.add("-in");
        commands.add(fastaFile.toString());

        commands.add("-logfile");
        commands.add(Paths.get(dbFolder.toString(), (blastDbName + ".log")).toString());

        commands.add("-out");
        commands.add(Paths.get(dbFolder.toString(), blastDbName).toString());

        String commandString = Utils.join(" ", commands);
        Logger.info("Running 'makeblastdb': " + commandString);

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
}
