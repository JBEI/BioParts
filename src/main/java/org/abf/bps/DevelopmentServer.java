package org.abf.bps;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.predicate.PredicatesHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.HttpString;
import org.abf.bps.servlet.WorsServletContextListener;
import org.glassfish.jersey.servlet.ServletContainer;

public class DevelopmentServer {
    public static void main(String[] args) throws Exception {
        DeploymentInfo servletBuilder = Servlets.deployment()
            .setClassLoader(ClassLoader.getSystemClassLoader())
            .addListener(Servlets.listener(WorsServletContextListener.class))
            .setContextPath("/")
            .setDeploymentName("Web of Registries Search Platform")
//                .setResourceManager(new FileResourceManager(new File("src/main/webapp"), 10)) //.addWelcomePage("index.htm")
            .addServlets(
                Servlets.servlet("Jersey REST Servlet", ServletContainer.class)
                    .addInitParam("jersey.config.server.provider.packages", "org.abf.bps.rest")
                    .addInitParam("jersey.config.server.provider.scanning.recursive", "false")
                    .addInitParam("javax.ws.rs.Application", "org.abf.bps.rest.multipart.WorsApplication")
                    .setAsyncSupported(true)
                    .setEnabled(true)
                    .addMapping("/rest/*")
            );

        // deploy servlet
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        HttpHandler servletHandler = manager.start();

        PredicatesHandler handler = Handlers.predicates(PredicatedHandlersParser.parse(
            "path-prefix('search') and path-prefix('annotate') and regex('/.+') -> rewrite('/')",
            ClassLoader.getSystemClassLoader()), servletHandler);

        PathHandler path = Handlers.path(Handlers.redirect("/"))
            .addPrefixPath("/", handler);

        Undertow server = Undertow.builder()
            .addHttpListener(8082, "localhost")
            .setHandler(exchange -> {
                exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
                exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET,POST, PUT, OPTIONS");
                exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "accept, authorization, content-type, x-requested-with");
                path.handleRequest(exchange);
            })
            .build();
        server.start();
    }
}
