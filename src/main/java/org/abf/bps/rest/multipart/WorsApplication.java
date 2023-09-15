package org.abf.bps.rest.multipart;

import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Hector Plahar
 */
public class WorsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        // register resources and features
        classes.add(MultiPartFeature.class);
        return classes;
    }
}
