package org.abf.bps.lib.part;

import org.abf.bps.lib.common.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.Optional;

public class SynbioClient {

    private static SynbioClient INSTANCE;
    private final Client client;

    private SynbioClient() {
        client = ClientBuilder.newClient();
        client.register(SynbioHubResultListHandler.class);
    }

    public static SynbioClient getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SynbioClient();
        return INSTANCE;
    }

    public DNASequence getGenbank(String uri) {
        String string;
        try {
            Invocation.Builder invocationBuilder = client.target(uri + "/gb").request(MediaType.APPLICATION_JSON_TYPE);
            string = invocationBuilder.buildGet().invoke(String.class);
        } catch (Exception e) {
            Logger.info("Error retrieving sequence for synbiohub part");
            return null;
        }

        if (StringUtils.isEmpty(string))
            return null;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes())) {
            LinkedHashMap<String, DNASequence> sequences = GenbankReaderHelper.readGenbankDNASequence(inputStream);
            Optional<DNASequence> optional = sequences.values().stream().findFirst();
            return optional.isEmpty() ? null : optional.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
