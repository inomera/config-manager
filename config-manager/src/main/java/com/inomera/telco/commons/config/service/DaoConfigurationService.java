package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Retrieves configurations from DAO layer and enables to update them.
 *
 * @author Serdar Kuzucu
 */
public class DaoConfigurationService implements ConfigurationUpdateService, ConfigurationFetcherService {
    private static final Logger LOG = LoggerFactory.getLogger(DaoConfigurationService.class);
    private static final String LOCK_NAME_CONFIGURATION_UPDATE = "DaoConfigurationService.ConfigurationUpdateLock";

    private final LockProvider lockProvider;
    private final TransactionTemplate transactionTemplate;
    private final ConfigurationDao configurationDao;
    private final int concatConfigurationMaxLength;

    /**
     * Creates a new DaoConfigurationService instance
     *
     * @param transactionTemplate          TransactionTemplate which will be used to handle transactions
     * @param lockProvider                 LockProvider will be used during update operations to provide thread safety
     * @param configurationDao             DAO to fetch configuration from
     * @param concatConfigurationMaxLength Maximum length of value column in database
     */
    public DaoConfigurationService(TransactionTemplate transactionTemplate, LockProvider lockProvider, ConfigurationDao configurationDao, int concatConfigurationMaxLength) {
        this.lockProvider = lockProvider;
        this.transactionTemplate = transactionTemplate;
        this.configurationDao = configurationDao;
        this.concatConfigurationMaxLength = concatConfigurationMaxLength;
    }

    @Override
    public void updateConfigurations(Map<String, Object> configurationsToUpdate) {
        final Set<String> keys = configurationsToUpdate.keySet();
        LOG.info("updateConfigurations::{}", keys);

        final Locked locked = lockProvider.lock(LOCK_NAME_CONFIGURATION_UPDATE);
        try {
            transactionTemplate.execute(status -> {
                configurationsToUpdate.forEach((key, value) -> {
                    final String valueToSet = Optional.ofNullable(value).map(Object::toString).orElse(StringUtils.EMPTY);
                    updateOrCreateConfiguration(key, valueToSet);
                });
                return null;
            });
        } finally {
            locked.unlock();
        }
    }

    @Override
    public void updateConcatConfiguration(String prefix, String value) {
        LOG.info("updateConcatConfiguration::{}", prefix);

        final Locked locked = lockProvider.lock(LOCK_NAME_CONFIGURATION_UPDATE);
        try {
            final List<String> values = splitIntoEqualLengths(value);
            final AtomicInteger valueIndex = new AtomicInteger(0);

            transactionTemplate.execute(status -> {
                configurationDao.deleteConfigurationsByPrefix(prefix);

                values.forEach(valueToSet -> {
                    final String key = prefix + "." + valueIndex.getAndIncrement();
                    createNewConfiguration(key, valueToSet);
                });
                return null;
            });
        } finally {
            locked.unlock();
        }
    }

    @Override
    public Map<String, String> fetchConfiguration() {
        return configurationDao.findAllConfigurations();
    }

    private void updateOrCreateConfiguration(String key, String value) {
        final int updatedRowCount = configurationDao.updateConfiguration(key, value);
        if (updatedRowCount == 0) {
            createNewConfiguration(key, value);
        }
    }

    private void createNewConfiguration(String key, String value) {
        configurationDao.insertNewConfiguration(key, value);
    }

    private List<String> splitIntoEqualLengths(String value) {
        final List<String> listOfValues = new ArrayList<>();
        if (StringUtils.isBlank(value)) {
            return listOfValues;
        }

        int start = 0;
        int end = concatConfigurationMaxLength;

        String substr = StringUtils.substring(value, start, end);
        while (StringUtils.isNotBlank(substr)) {
            listOfValues.add(substr);

            start += concatConfigurationMaxLength;
            end += concatConfigurationMaxLength;
            substr = StringUtils.substring(value, start, end);
        }

        return listOfValues;
    }
}
