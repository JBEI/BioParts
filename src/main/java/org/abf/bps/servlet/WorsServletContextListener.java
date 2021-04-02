package org.abf.bps.servlet;

import org.abf.bps.lib.executor.IceExecutorService;
import org.abf.bps.lib.search.blast.RebuildIndexes;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Hector Plahar
 */
public class WorsServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        IceExecutorService.getInstance().stopService();
    }
}
