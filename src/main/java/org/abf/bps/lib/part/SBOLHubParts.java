package org.abf.bps.lib.part;

import org.abf.bps.lib.dto.entry.EntryType;
import org.abf.bps.lib.dto.entry.PartData;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.utils.SeqUtils;
import org.biojava.nbio.core.sequence.DNASequence;

public class SBOLHubParts {

    private final SynbioClient client;

    public SBOLHubParts() {
        this.client = SynbioClient.getInstance();
    }

    public PartSequence get(String uri, String uuid) {
        DNASequence sequence = client.getGenbank(uri);
        PartSequence partSequence = new PartSequence();
        PartData partData = new PartData(EntryType.PART);

        if (sequence != null) {
            partSequence.setSequence(SeqUtils.convert(sequence));
            partData.setHasSequence(true);
            partData.setShortDescription(sequence.getDescription());
            partData.setPartId(sequence.getAccession().toString());
            partData.setName(partData.getPartId());
        }

        PartSource source = new PartSource(uri, "SynbioHUB", partData.getPartId());
        partSequence.setPartSource(source);
        partData.setRecordId(uuid);

        partSequence.setPart(partData);
        return partSequence;
    }
}
