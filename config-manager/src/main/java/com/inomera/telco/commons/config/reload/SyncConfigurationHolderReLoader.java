package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synchronously reloads configurations.
 *
 * @author Serdar Kuzucu
 */
public class SyncConfigurationHolderReLoader implements ConfigurationHolderReLoader {
    private static final Logger LOG = LoggerFactory.getLogger(SyncConfigurationHolderReLoader.class);

    private final ConfigurationHolder configurationHolder;

    public SyncConfigurationHolderReLoader(ConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }

    @Override
    public void reloadConfigurations() {
        LOG.info("reloadConfigurations started");

        configurationHolder.reloadConfigurations();

        LOG.info("reloadConfigurations finished");
    }
}
