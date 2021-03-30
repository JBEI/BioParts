package org.abf.bps.rest;

import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.FeaturedDNASequence;
import org.abf.bps.lib.dto.Registry;
import org.abf.bps.lib.dto.entry.PartSequence;
import org.abf.bps.lib.dto.search.SearchQuery;
import org.abf.bps.lib.dto.search.SearchResult;
import org.abf.bps.lib.dto.search.SearchResults;
import org.abf.bps.lib.index.RemoteGenBankPart;
import org.abf.bps.lib.index.SearchIndex;
import org.abf.bps.lib.part.Parts;
import org.abf.bps.lib.scrape.AddGeneParts;
import org.abf.bps.lib.scrape.IgemParts;
import org.abf.bps.lib.search.blast.Constants;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST resource for searching. Supports keyword search with query params for filtering and advanced search
 *
 * @author Hector Plahar
 */
@Path("/search")
public class SearchResource extends RestResource {

    @Path("/{partId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findPart(@PathParam("partId") String partId) {
        Parts parts = new Parts();
        return super.respond(parts.get(partId));
    }

    @Path("/{partId}/sequence")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findPartSequence(@PathParam("partId") String partId) {
        try {
            SearchIndex index = new SearchIndex();
            SearchResult searchResult = index.getByRecordId(partId);
            if (searchResult == null)
                return super.respond(null);

            final String url = searchResult.getPartner().getUrl();

            if (url.startsWith(Constants.IGEM_PART_URL_PREFIX)) {
                PartSequence sequence = IgemParts.parseIgemPart(Constants.IGEM_XML_PART_URL_PREFIX + searchResult.getEntryInfo().getName());
                if (sequence == null)
                    return super.respond(null);

                return super.respond(sequence.getSequence());
            }

            if (url.startsWith(Constants.ADDGENE_URL_PREFIX)) {
                PartSequence sequence = new AddGeneParts().retrievePlasmid(searchResult.getEntryInfo().getPartId());
                return super.respond(sequence.getSequence());
            }

            if (url.startsWith(Constants.NCBI_SEARCH_URL)) {
                RemoteGenBankPart remoteGenBankPart = new RemoteGenBankPart();
                return super.respond(remoteGenBankPart.getSequence(searchResult.getEntryInfo().getPartId()));
            }

            if (url.equalsIgnoreCase(Constants.MASTER_REGISTRY_URL)) {
                FeaturedDNASequence sequence = RestClient.getInstance().get(url, "/rest/parts/" + partId + "/sequence", FeaturedDNASequence.class, null);
                return super.respond(sequence);
            }

            // todo : this endpoint will most likely be updated
            FeaturedDNASequence sequence = RestClient.getInstance().get(Constants.MASTER_REGISTRY_URL,
                "/rest/partners/" + searchResult.getPartner().getId() + "/" + searchResult.getEntryInfo().getId()
                    + "/sequence", FeaturedDNASequence.class, null);
            return super.respond(sequence);
        } catch (Exception e) {
            Logger.error(e);
            return super.respond(null);
        }
    }

    /**
     * Advanced Search. The use of post is mostly for the sequence string for blast which can get
     * very long and results in a 413 status code if sent via GET
     *
     * @param query parameters to the search
     * @return results of the search
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response search(SearchQuery query) {
        try {
            SearchIndex index = new SearchIndex();
            SearchResults results = index.find(query);
            return Response.status(Response.Status.OK).entity(results).build();
        } catch (Exception e) {
            return super.respond(null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/registries")
    public Response addRegistry(Registry registry) {
        // write registry data to file
        Logger.info("New registry add request");
        Logger.info(registry.getUserName() + "<" + registry.getUserEmail() + ">");
        Logger.info(registry.getName());
        Logger.info(registry.getUrl());
        Logger.info(registry.getDetails());
        return Response.status(Response.Status.OK).build();
    }
}
