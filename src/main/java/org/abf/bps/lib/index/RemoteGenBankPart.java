package org.abf.bps.lib.index;

import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.abf.bps.lib.parsers.genbank.GenBankParser;
import org.abf.bps.lib.search.blast.Constants;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @author Hector Plahar
 * GenBank Part that is locally indexed but whose sequence (and associated) information
 * resides on the ncbi server
 */
public class RemoteGenBankPart {

    public FeaturedDNASequence getSequence(String sequenceId) throws Exception {

        try (InputStream is = new URL(Constants.NCBI_SEARCH_URL + "/" + sequenceId + "/?report=genbank").openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            Stream<String> stream = reader.lines();
            Iterator<String> it = stream.iterator(); // Files.lines(Paths.get("/home/hector/Downloads/AC163214.fasta")).iterator();
            boolean start = false;
            StringBuilder g = new StringBuilder();

            while (it.hasNext()) {
                String line = Jsoup.parse(it.next()).text();

                if (!line.startsWith("LOCUS")) {
                    if (!start) {
                        continue;
                    }
                } else {
                    // locus encountered
                    start = true;
                }

                g.append(line).append("\n");

                if (line.startsWith("//"))
                    break;
            }

            GenBankParser parser = new GenBankParser();

//            PartData partData = new PartData(EntryType.PART);
//            partData.setHasSequence(true);
//            partData.setName(sequence.getName());
//            partData.setPartId(sequence.getIdentifier());
//            partData.setRecordId(sequence.getIdentifier());
//            partData.setCreator(sequence.getAuthors());
//            partData.setShortDescription(sequence.getDescription());
//
//            try {
//                Date date = new SimpleDateFormat("dd-MMM-yyyy").parse(sequence.getDate());
//                partData.setCreationTime(date.getTime());
//            } catch (Exception e) {
//                //
//            }

            return parser.parse(g.toString());
        }
    }
}
