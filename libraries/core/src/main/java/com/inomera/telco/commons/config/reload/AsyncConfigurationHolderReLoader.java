package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

/**
 * Use this to reload configurations asynchronously.
 *
 * @author Serdar Kuzucu
 */
@Slf4j
public class AsyncConfigurationHolderReLoader implements ConfigurationHolderReLoader {
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
            LOG.debug("reloadConfigurations started");

            try {
                delegate.reloadConfigurations();
            } catch (Exception e) {
                LOG.error("Error in reloadConfigurations: {}", e.getMessage(), e);
                return;
            }

            LOG.debug("reloadConfigurations finished");
        });
    }
}
