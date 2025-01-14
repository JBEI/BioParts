package org.abf.bps.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import org.abf.bps.lib.dto.IDataTransferModel;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Hector Plahar
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArrayDataJSONHandler implements MessageBodyWriter<ArrayList<IDataTransferModel>>,
    MessageBodyReader<ArrayList<IDataTransferModel>> {
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType,
                              final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }

    @Override
    public ArrayList<IDataTransferModel> readFrom(final Class<ArrayList<IDataTransferModel>> type,
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
    public long getSize(final ArrayList<IDataTransferModel> iDataTransferModels,
                        final Class<?> type, final Type genericType, final Annotation[] annotations,
                        final MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(final ArrayList<IDataTransferModel> data, final Class<?> type,
                        final Type genericType, final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
        throws IOException, WebApplicationException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer out = new OutputStreamWriter(entityStream)) {
            gson.toJson(data, out);
        }
    }
}
