package org.abf.bps.lib.part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SynbioHubResultListHandler implements MessageBodyWriter<ArrayList<SynbioHubPart>>,
    MessageBodyReader<ArrayList<SynbioHubPart>> {
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType,
                              final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }

    @Override
    public ArrayList<SynbioHubPart> readFrom(final Class<ArrayList<SynbioHubPart>> type,
                                             final Type genericType, final Annotation[] annotations, final MediaType mediaType,
                                             final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
        throws IOException, WebApplicationException {
        final Gson gson = new GsonBuilder().create();
        try (Reader in = new InputStreamReader(entityStream)) {
            return gson.fromJson(in, type);
        }
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType,
                               final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(final ArrayList<SynbioHubPart> SynbioHubParts,
                        final Class<?> type, final Type genericType, final Annotation[] annotations,
                        final MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(final ArrayList<SynbioHubPart> data, final Class<?> type,
                        final Type genericType, final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
        throws IOException, WebApplicationException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer out = new OutputStreamWriter(entityStream)) {
            gson.toJson(data, out);
        }
    }
}
