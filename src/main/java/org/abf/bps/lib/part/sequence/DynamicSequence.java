package org.abf.bps.lib.part.sequence;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.dto.search.SearchResult;
import org.abf.bps.lib.index.SearchIndex;
import org.abf.bps.lib.part.Parts;
import org.abf.bps.lib.part.sequence.formatters.FastaFormatter;
import org.abf.bps.lib.part.sequence.formatters.GFF3Formatter;
import org.abf.bps.lib.part.sequence.formatters.GenbankFormatter;
import org.abf.bps.lib.part.sequence.formatters.IFormatter;

import java.io.ByteArrayOutputStream;

/**
 * Sequence that can be converted from one form (e.g. genbank) to another (e.g. fasta)
 *
 * @author Hector Plahar
 */
public class DynamicSequence {

    private final String recordId;

    public DynamicSequence(String recordId) {
        this.recordId = recordId;
    }

    public ByteArrayWrapper convert(SequenceFormat format) {
        try {
            SearchIndex index = new SearchIndex();
            SearchResult searchResult = index.getByRecordId(recordId);
            if (searchResult == null)
                return new ByteArrayWrapper(new byte[]{'\0'}, "no_sequence");


            Parts parts = new Parts();
            PartSequence partSequence = parts.get(recordId);
            if (partSequence == null || partSequence.getSequence() == null)
                return new ByteArrayWrapper(new byte[]{'\0'}, "no_sequence");

            String name;
            String sequenceString;
            FeaturedDNASequence dnaSequence = partSequence.getSequence();


            switch (format) {
                case GENBANK:
                default:
                    GenbankFormatter genbankFormatter = new GenbankFormatter(dnaSequence.getIdentifier());
                    genbankFormatter.setCircular(dnaSequence.isCircular());
                    sequenceString = compose(dnaSequence, genbankFormatter);
                    name = dnaSequence.getIdentifier() + ".gb";
                    break;

                case FASTA:
                    FastaFormatter formatter = new FastaFormatter();
                    sequenceString = compose(dnaSequence, formatter);
                    name = dnaSequence.getIdentifier() + ".fa";
                    break;

                case GFF3:
                    sequenceString = compose(dnaSequence, new GFF3Formatter());
                    name = dnaSequence.getIdentifier() + ".gff3";
                    break;
            }
            return new ByteArrayWrapper(sequenceString.getBytes(), name);
        } catch (Exception e) {
            Logger.error("Failed to generate genbank file for download!", e);
            return new ByteArrayWrapper(new byte[]{'\0'}, "sequence_error");
        }
    }

    /**
     * Generate a formatted text of a given {@link IFormatter} from the given {@link FeaturedDNASequence}.
     *
     * @param sequence
     * @param formatter
     * @return Text of a formatted sequence.
     */
    protected String compose(FeaturedDNASequence sequence, IFormatter formatter) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            formatter.format(sequence, byteStream);
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
        return byteStream.toString();
    }
}
