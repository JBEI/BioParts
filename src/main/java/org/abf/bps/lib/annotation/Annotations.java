package org.abf.bps.lib.annotation;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.DNAFeature;
import org.abf.bps.lib.dto.DNAFeatureLocation;
import org.abf.bps.lib.dto.search.BlastQuery;
import org.abf.bps.lib.search.blast.BlastException;
import org.abf.bps.lib.search.blast.BlastSearch;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Annotations {

    private static final String DB_NAME = "annotations";

    /**
     * Run a blast query against the sequence features blast database.
     *
     * @param query wrapper around sequence to blast
     * @return list of DNA features that match the query according to the parameters
     * @throws BlastException on null result or exception processing the result
     */
    public List<DNAFeature> runBlast(BlastQuery query, String perc_identity) throws BlastException {   // todo add e-value
        BlastSearch blastSearch = new BlastSearch(AnnotationsBlastFile.indexPath(), DB_NAME);
        if (StringUtils.isBlank(perc_identity))
            perc_identity = "100";
        String result = blastSearch.run(query, "-perc_identity", perc_identity,
            "-outfmt", "10 stitle qstart qend sstart send sstrand");
        if (result == null)
            throw new BlastException("Exception running blast");
        return processBlastOutput(result);
    }

    /**
     * Process the output of the blast run for features
     * into a list of feature objects
     * <br>
     * Expected format for the output (per line) is
     * <code>feature_id, label, type, qstart, qend, sstart, send, sstrand</code>
     * Therefore line[0] is feature_id, line[1] is label etc
     * <br>Since we are only interested in features that have a full match (covers entire feature) some matches are
     * manually eliminated. The results returned by blast can cover only a subset of the sequence. e.g.
     * given query = 'ATGC' and feature1 = 'ATG' and feature2 = 'TATGT', the query will return
     * 1,3,1,3 and 1,3,2,4.
     *
     * @param blastOutput blast program output
     * @return list of feature objects resulting from processing the blast output
     */
    private List<DNAFeature> processBlastOutput(String blastOutput) {
        List<DNAFeature> hashMap = new ArrayList<>();
        HashSet<String> duplicates = new HashSet<>();

        try (CSVReader reader = new CSVReader(new StringReader(blastOutput))) {
            List<String[]> lines = reader.readAll();

            for (String[] line : lines) {
                if (line.length != 7) {
                    continue;
                }

//                long id = Long.decode(line[0]);
                String label = line[0];
                String type = line[1];
                int queryStart = Integer.decode(line[2]);
                int queryEnd = Integer.decode(line[3]);
                int subjectStart = Integer.decode(line[4]);
                int subjectEnd = Integer.decode(line[5]);
                int strand = "plus".equalsIgnoreCase(line[6]) ? 1 : -1;

                if (!duplicates.add(type + ":" + queryStart + ":" + queryEnd + ":" + strand)) {
                    continue;
                }

                if (subjectStart != 1 && (queryEnd - queryStart) + 1 != subjectEnd)
                    continue;

                // check for full feature coverage
                DNAFeature dnaFeature = new DNAFeature();
//                dnaFeature.setId(id);
                dnaFeature.setName(label);
                dnaFeature.setType(type);
                DNAFeatureLocation location = new DNAFeatureLocation();
                location.setGenbankStart(queryStart);
                location.setEnd(queryEnd);
                dnaFeature.getLocations().add(location);
                dnaFeature.setStrand(strand);
                hashMap.add(dnaFeature);
            }

            return hashMap;
        } catch (IOException | CsvException e) {
            Logger.error(e);
            return null;
        }
    }
}
