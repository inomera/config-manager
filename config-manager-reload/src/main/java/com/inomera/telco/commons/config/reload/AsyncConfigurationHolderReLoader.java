package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Use this to reload configurations asynchronously.
 *
 * @author Serdar Kuzucu
 */
public class AsyncConfigurationHolderReLoader implements ConfigurationHolderReLoader {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncConfigurationHolderReLoader.class);

    private final ConfigurationHolderReLoader delegate;
    private final Executor executor;

    public AsyncConfigurationHolderReLoader(ConfigurationHolderReLoader delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    public AsyncConfigurationHolderReLoader(ConfigurationHolder configurationHolder, Executor executor) {
        this(new SyncConfigurationHolderReLoader(configurationHolder), executor);
    }

    @Override
    public void reloadConfigurations() {
        executor.execute(() -> {
            LOG.info("reloadConfigurations started");

            try {
                delegate.reloadConfigurations();
            } catch (Exception e) {
                LOG.error("Error in reloadConfigurations: {}", e.getMessage(), e);
                return;
            }

            LOG.info("reloadConfigurations finished");
        });
    }
}
