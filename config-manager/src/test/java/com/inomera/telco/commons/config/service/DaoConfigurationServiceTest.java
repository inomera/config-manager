package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DaoConfigurationServiceTest {
    private static final int MAX_LENGTH = 10;

    @Mock
    private LockProvider lockProvider;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ConfigurationDao configurationDao;

    @Mock
    private Locked locked;

    private DaoConfigurationService service;

    @BeforeEach
    void init() {
        this.service = new DaoConfigurationService(transactionTemplate, lockProvider, configurationDao, MAX_LENGTH);
        when(lockProvider.lock(any())).thenReturn(locked);
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            final TransactionCallback<?> action = invocation.getArgument(0);
            return action.doInTransaction(null);
        });
    }

    @Test
    @DisplayName("Should call update on DAO layer for all keys to be updated")
    void shouldCallUpdateOnDaoForAllKeysToUpdate() {
        final HashMap<String, Object> configurationsToUpdate = new HashMap<>();
        configurationsToUpdate.put("a", "1");
        configurationsToUpdate.put("b", "2");
        configurationsToUpdate.put("c", "3");

        when(configurationDao.updateConfiguration(any(), any())).thenReturn(1);

        service.updateConfigurations(configurationsToUpdate);

        verify(configurationDao, times(1)).updateConfiguration("a", "1");
        verify(configurationDao, times(1)).updateConfiguration("b", "2");
        verify(configurationDao, times(1)).updateConfiguration("c", "3");
    }

    @Test
    @DisplayName("Should call insert on DAO layer for keys which does not exist")
    void shouldCallInsertOnDaoForKeysCannotBeUpdated() {
        final HashMap<String, Object> configurationsToUpdate = new HashMap<>();
        configurationsToUpdate.put("a", "1");
        configurationsToUpdate.put("b", "2");
        configurationsToUpdate.put("c", "3");

        when(configurationDao.updateConfiguration(any(), any())).thenReturn(1);
        when(configurationDao.updateConfiguration(eq("b"), any())).thenReturn(0);

        service.updateConfigurations(configurationsToUpdate);

        verify(configurationDao, times(1)).updateConfiguration("a", "1");
        verify(configurationDao, times(1)).updateConfiguration("b", "2");
        verify(configurationDao, times(1)).updateConfiguration("c", "3");

        verify(configurationDao, never()).insertNewConfiguration(eq("a"), any());
        verify(configurationDao, times(1)).insertNewConfiguration("b", "2");
        verify(configurationDao, never()).insertNewConfiguration(eq("c"), any());
    }

    @Test
    @DisplayName("Should split concatenated configuration value into parts correctly")
    void shouldSplitConcatConfigurationCorrectly() {
        service.updateConcatConfiguration("key", "abcdefghijklmnopqrstuvwxyz");

        verify(configurationDao, times(1)).deleteConfigurationsByPrefix("key");
        verify(configurationDao, times(1)).insertNewConfiguration("key.0", "abcdefghij");
        verify(configurationDao, times(1)).insertNewConfiguration("key.1", "klmnopqrst");
        verify(configurationDao, times(1)).insertNewConfiguration("key.2", "uvwxyz");
        verify(configurationDao, never()).updateConfiguration(any(), any());
    }

    @Test
    @DisplayName("Should not insert or update anything when value is empty in concat configuration")
    void shouldDeleteExistingConfigurationButNotInsertAnyWhenValueIsEmpty() {
        service.updateConcatConfiguration("key", "");

        verify(configurationDao, times(1)).deleteConfigurationsByPrefix("key");
        verify(configurationDao, never()).updateConfiguration(any(), any());
        verify(configurationDao, never()).insertNewConfiguration(any(), any());
    }

    @Test
    @DisplayName("Should return all configuration coming from DAO")
    void shouldReturnAllConfigurationComingFromDAO() {
        final Map<String, String> configurationFromDao = new HashMap<>();
        configurationFromDao.put("a", "1");
        configurationFromDao.put("b", "2");
        configurationFromDao.put("c", "3");
        when(configurationDao.findAllConfigurations()).thenReturn(configurationFromDao);

        final Map<String, String> configuration = service.fetchConfiguration();
        assertEquals(configurationFromDao, configuration);
    }
}
