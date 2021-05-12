package org.abf.bps.lib.part;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.abf.bps.lib.dto.entry.PartData;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.dto.search.SearchResult;
import org.abf.bps.lib.index.RemoteGenBankPart;
import org.abf.bps.lib.index.SearchIndex;
import org.abf.bps.lib.search.blast.Constants;
import org.abf.bps.rest.RestClient;

public class Parts {

    public PartSequence get(String recordId) {
        try {
            SearchIndex index = new SearchIndex();
            SearchResult searchResult = index.getByRecordId(recordId);
            if (searchResult == null)
                return null;

            String partnerUrl = searchResult.getPartner().getUrl();

            if (partnerUrl.startsWith(Constants.IGEM_PART_URL_PREFIX)) {
                return IgemParts.parseIgemPart(searchResult.getEntryInfo().getName());
            }

            if (partnerUrl.startsWith(Constants.NCBI_SEARCH_URL)) {
                RemoteGenBankPart remoteGenBankPart = new RemoteGenBankPart();
                FeaturedDNASequence sequence = remoteGenBankPart.getSequence(searchResult.getEntryInfo().getPartId());
                return new PartSequence(searchResult.getEntryInfo(), sequence);
            }

            if (partnerUrl.startsWith(Constants.ADDGENE_URL_PREFIX)) {
                PartSequence sequence = new AddGeneParts().retrievePlasmid(searchResult.getEntryInfo().getPartId());
                sequence.getPart().setRecordId(searchResult.getEntryInfo().getRecordId());
                return sequence;
            }

            if (partnerUrl.equalsIgnoreCase(Constants.MASTER_REGISTRY_URL)) {
                PartData partData = RestClient.getInstance().get(Constants.MASTER_REGISTRY_URL, "/rest/parts/" + recordId, PartData.class, null);
                FeaturedDNASequence sequence = null;
                if (partData.isHasSequence()) {
                    sequence = RestClient.getInstance().get(partnerUrl, "/rest/parts/" + recordId + "/sequence", FeaturedDNASequence.class, null);
                }
                return new PartSequence(partData, sequence);
            }

            if (partnerUrl.startsWith(Constants.SYNBIO_HUB_URL)) {
                SynbioHubPart part = new SynbioHubPart();
                part.setUri(partnerUrl);
                part.setName(searchResult.getEntryInfo().getName());
                part.setDescription(searchResult.getEntryInfo().getShortDescription());
                return new SBOLHubParts().get(part);
            }

            PartData data = RestClient.getInstance().get(Constants.MASTER_REGISTRY_URL, "/rest/web/"
                + searchResult.getPartner().getId()
                + "/entries/" + searchResult.getEntryInfo().getId(), PartData.class, null);
            FeaturedDNASequence sequence = RestClient.getInstance().get(Constants.MASTER_REGISTRY_URL,
                "/rest/partners/" + searchResult.getPartner().getId() + "/" + searchResult.getEntryInfo().getId()
                    + "/sequence", FeaturedDNASequence.class, null);
            return new PartSequence(data, sequence);
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }
}
