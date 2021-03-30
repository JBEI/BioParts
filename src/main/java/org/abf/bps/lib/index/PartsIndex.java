package org.abf.bps.lib.index;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.part.Parts;
import org.abf.bps.lib.scrape.AddGeneParts;
import org.abf.bps.lib.scrape.GenBankParts;
import org.abf.bps.lib.scrape.IceParts;
import org.abf.bps.lib.scrape.IgemParts;
import org.abf.bps.lib.search.blast.BlastPlus;

import java.io.Closeable;
import java.io.IOException;

// come up with a better name
public class PartsIndex implements Closeable {

    private final BlastPlus blastIndex;
    private final DocumentIndex index;

    public PartsIndex(boolean createNew) throws IOException {
        blastIndex = new BlastPlus();
        index = new DocumentIndex(createNew);
    }

    public static void main(String[] args) throws Exception {
        try (PartsIndex index = new PartsIndex(true)) {
            index.rebuildAll();
        }
    }

    public void rebuildAll() {
//        try {
//            new IGemPartsFromList(blastIndex, index).index();
//        } catch (IOException e) {
//            Logger.error(e);
//        }

//        Parts parts = registerPartSources();
//        int currentCount = 0;
//        while (parts.hasNext()) {
//            PartSequence partSequence = parts.next();
//            if (partSequence == null)
//                continue;
//
//            if (maxPartsPerSource != 0 && ++currentCount == maxPartsPerSource) {
//                parts.skipCurrentPartSource();
//                currentCount = 0;
//            }
//
//            try {
//                index.add(partSequence.getPart(), partSequence.getPartSource());
//                blastIndex.writeSequenceToFasta(partSequence.getPart(), partSequence.getSequence());
//            } catch (Exception e) {
//                Logger.error(e);
//            }
//        }
    }

    /**
     * Sources of parts to index. Currently includes
     * <br>
     * <ul>
     * <li>AddGene</li>
     * <li>GenBank</li>
     * <li>Ice Parts</li>
     * <li>iGem</li>
     * </ul>
     *
     * @return parts list
     */
    private Parts registerPartSources() {
        Parts parts = new Parts();

        parts.registerSource(new AddGeneParts());
        try {
            parts.registerSource(new GenBankParts());
        } catch (IOException e) {
            Logger.error(e);
        }
        parts.registerSource(new IceParts());
        parts.registerSource(new IgemParts());
        return parts;
    }

    @Override
    public void close() throws IOException {
        index.close();
        blastIndex.close();
    }
}
