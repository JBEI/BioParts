package org.abf.bps.rest;

import com.google.common.io.ByteStreams;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.abf.bps.lib.part.sequence.ByteArrayWrapper;
import org.abf.bps.lib.part.sequence.DynamicSequence;
import org.abf.bps.lib.part.sequence.SequenceFormat;

import java.io.ByteArrayInputStream;

/**
 * Rest resource for handling file downloads
 *
 * @author Hector Plahar
 */
@Path("/file")
public class FileResource extends RestResource {
    @GET
    @Path("{recordId}/sequence/{type}")
    public Response downloadSequence(
        @PathParam("recordId") final String recordId,
        @PathParam("type") final String downloadType) {
        DynamicSequence sequence = new DynamicSequence(recordId);

        final ByteArrayWrapper wrapper = sequence.convert(SequenceFormat.fromString(downloadType));
        if (wrapper == null)
            return super.respond(null);

        StreamingOutput stream = output -> {
            final ByteArrayInputStream input = new ByteArrayInputStream(wrapper.getBytes());
            ByteStreams.copy(input, output);
        };

        return addHeaders(Response.ok(stream), wrapper.getName());
    }
}
