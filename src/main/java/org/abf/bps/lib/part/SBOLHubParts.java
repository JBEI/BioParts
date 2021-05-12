package org.abf.bps.lib.part;

import org.abf.bps.lib.dto.entry.EntryType;
import org.abf.bps.lib.dto.entry.PartData;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.utils.SeqUtils;
import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.core.sequence.DNASequence;

import java.util.UUID;

public class SBOLHubParts {

    private final SynbioClient client;

    public SBOLHubParts() {
        this.client = SynbioClient.getInstance();
    }

    public PartSequence get(SynbioHubPart part) {
        DNASequence sequence = client.getGenbank(part.getUri());
        PartSequence partSequence = new PartSequence();
        PartData partData = new PartData(EntryType.PART);

        if (sequence != null) {
            partSequence.setSequence(SeqUtils.convert(sequence));
            partData.setHasSequence(true);
        }

        PartSource source = new PartSource(part.getUri(), "SynbioHUB", part.getDisplayId());
        partSequence.setPartSource(source);

        partData.setRecordId(UUID.randomUUID().toString());
        partData.setName(part.getName());
        partData.setShortDescription(part.getDescription());
        if (StringUtils.isEmpty(part.getDisplayId())) {
            partData.setPartId(part.getName());
        } else {
            partData.setPartId(part.getDisplayId());
        }
        partSequence.setPart(partData);
        return partSequence;
    }
}
