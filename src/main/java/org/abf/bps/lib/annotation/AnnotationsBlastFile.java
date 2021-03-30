package org.abf.bps.lib.annotation;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.DNAFeature;
import org.abf.bps.lib.search.blast.BlastCommandType;
import org.abf.bps.lib.search.blast.BlastCommands;
import org.abf.bps.lib.search.blast.Constants;
import org.apache.commons.lang3.StringUtils;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.abf.bps.lib.search.blast.BlastPlus.breakUpLines;

public class AnnotationsBlastFile {

    public static final String ANNOTATION_FASTA_FILE = "annotations.fasta";
    private final Path path;
    protected static final String DELIMITER = ",";

    public AnnotationsBlastFile() throws IOException {
        this.path = Paths.get(Constants.DATA_DIR, "annotations");
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    public static Path indexPath() {
        return Paths.get(Constants.DATA_DIR, "annotations", "db");
    }

    // create the blast database using generated fasta file
    public void createBlastDb() {
        Path fastaFile = Paths.get(this.path.toString(), ANNOTATION_FASTA_FILE);
        if (!Files.exists(fastaFile)) {
            Logger.error("No annotations fasta file available at " + fastaFile.toString());
            return;
        }

        // check database folder existence
        Path dbFolder = Paths.get(this.path.toString(), "db");
        try {
            if (!Files.exists(dbFolder))
                Files.createDirectory(dbFolder);
        } catch (IOException e) {
            Logger.error("Error create blast database folder", e);
            return;
        }

        // run blast commands to build database
        BlastCommands commands = new BlastCommands(BlastCommandType.MAKE_BLAST_DB, dbFolder, "annotations",
            fastaFile);
        try {
            commands.execute();
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    // convert to
    public void add(DNAFeature feature) throws IOException {
        if (StringUtils.isEmpty(feature.getName()))
            return;

        String fastaString = getSequenceFasta(feature);
        if (fastaString == null)
            return;

        Path fastaFile = Paths.get(path.toString(), ANNOTATION_FASTA_FILE);
        Files.write(fastaFile, fastaString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static String getSequenceFasta(DNAFeature feature) {
        String sequenceString = "";
        String temp = feature.getSequence();
        if (StringUtils.isEmpty(temp))
            return null;

        SymbolList symL;
        try {
            symL = DNATools.createDNA(temp.trim());
        } catch (IllegalSymbolException e1) {
            // maybe it's rna?
            try {
                symL = RNATools.createRNA(temp.trim());
            } catch (IllegalSymbolException e2) {
                // skip this sequence
                Logger.debug(e2.toString());
                return null;
            }
        }

        sequenceString = breakUpLines(symL.seqString() + symL.seqString());

        if (StringUtils.isEmpty(sequenceString))
            return null;

        String idString = ">" + feature.getName();
        idString += DELIMITER + feature.getType();
        idString += "\n";
        return (idString + sequenceString + "\n");
    }
}
