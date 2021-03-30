package org.abf.bps.lib.index.igem;

import com.opencsv.CSVReader;
import org.abf.bps.lib.annotation.AnnotationsBlastFile;
import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.DNAFeature;
import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.index.DocumentIndex;
import org.abf.bps.lib.scrape.IgemParts;
import org.abf.bps.lib.search.blast.BlastPlus;
import org.abf.bps.lib.search.blast.Constants;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Indexing using the list of entries from the csv file
 */
public class IGemPartsFromList {

    private final Path path;
    private final BlastPlus blastIndex;
    private final DocumentIndex index;
    private final AnnotationsBlastFile annotationsBlastFile;

    public IGemPartsFromList(BlastPlus blastIndex, DocumentIndex index) throws IOException {
        String fileName = "iGemParts-partial.csv";

        path = Paths.get(Constants.IGEM_INPUT_DIR, fileName);
        this.blastIndex = blastIndex;
        this.index = index;
        this.annotationsBlastFile = new AnnotationsBlastFile();
    }

    public void index() throws IOException {
        InputStreamReader input = new FileReader(this.path.toFile());
        try (CSVReader reader = new CSVReader(input)) {

//            new Consumer<String[]>() {
//                @Override
//                public void accept(String[] strings) {
//
//                }
//            }

            reader.iterator().forEachRemaining(
                strings -> {
                    // first xter is part id
                    String partId = strings[0];
                    if (StringUtils.isEmpty(partId))
                        return;

                    try {
                        PartSequence partSequence = IgemParts.parseIgemPart(partId);
                        if (partSequence == null)
                            return;


                        try {
                            FeaturedDNASequence sequence = partSequence.getSequence();
                            index.add(partSequence.getPart(), partSequence.getPartSource());
                            blastIndex.writeSequenceToFasta(partSequence.getPart(), sequence);

                            // annotations index
                            for (DNAFeature feature : sequence.getFeatures()) {
                                if (StringUtils.isEmpty(feature.getName()))
                                    continue;

                                int gStart = feature.getLocations().get(0).getGenbankStart();
                                int end = feature.getLocations().get(0).getEnd();

                                String featureSequence = sequence.getSequence().substring(gStart - 1, end - 1);
                                feature.setSequence(featureSequence);

                                // index feature
                                this.annotationsBlastFile.add(feature);
                            }

                        } catch (Exception e) {
                            Logger.error(e);
                        }
                    } catch (Exception e) {
                        // exception from iGem parsing
                        Logger.error(e);
                    }
                });

            // create annotations blast db
            this.annotationsBlastFile.createBlastDb();
        }
    }
}
