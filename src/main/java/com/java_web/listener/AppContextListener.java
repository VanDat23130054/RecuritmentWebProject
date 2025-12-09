package com.java_web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java_web.config.DB;

/**
 * Application lifecycle listener
 * Handles initialization and cleanup of application resources
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("===========================================");
        logger.info("Application starting...");
        logger.info("Initializing HikariCP connection pool...");
        
        try {
            // Force initialization of DB connection pool
            DB.getDataSource();
            logger.info("HikariCP connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
        }
        
        logger.info("Application started successfully");
        logger.info("===========================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("===========================================");
        logger.info("Application shutting down...");
        logger.info("Closing HikariCP connection pool...");
        
        try {
            DB.close();
            logger.info("HikariCP connection pool closed successfully");
        } catch (Exception e) {
            logger.error("Error closing connection pool", e);
        }
        
        logger.info("Application shutdown complete");
        logger.info("===========================================");
    }
}
