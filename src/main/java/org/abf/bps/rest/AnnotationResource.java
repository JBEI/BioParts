package org.abf.bps.rest;

import org.abf.bps.lib.annotation.Annotations;
import org.abf.bps.lib.common.logging.Logger;
import org.abf.bps.lib.dto.DNAFeature;
import org.abf.bps.lib.dto.search.BlastQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/annotations")
public class AnnotationResource extends RestResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAnnotations(@DefaultValue("75") @QueryParam("perc_match") String match,
                                      BlastQuery query) {
        Annotations annotations = new Annotations();
        try {
            List<DNAFeature> features = annotations.runBlast(query, match);
            return super.respond(features);
        } catch (Exception e) {
            Logger.error(e);
            return super.respond(null);
        }
    }
}
