package com.example.capitalgains.service;

import com.example.capitalgains.model.Security;
import com.example.capitalgains.model.SecurityClass;
import com.example.capitalgains.model.SecurityType;
import com.example.capitalgains.repository.SecurityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private SecurityRepository securityRepository;

    @InjectMocks
    private SecurityService securityService;

    @Test
    void getAllSecurities_shouldReturnAllSecurities() {
        List<Security> expectedSecurities = List.of(
            Security.builder()
                .id(1L)
                .security_name("Security1")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.EQUITY)
                .build(),
            Security.builder()
                .id(2L)
                .security_name("Security2")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.DEBT)
                .build()
        );

        when(securityRepository.findAll()).thenReturn(expectedSecurities);

        List<Security> result = securityService.getAllSecurities();

        assertEquals(expectedSecurities, result);
        assertEquals(2, result.size());
        verify(securityRepository).findAll();
    }

    @Test
    void getAllSecurities_emptyList_shouldReturnEmptyList() {
        when(securityRepository.findAll()).thenReturn(List.of());

        List<Security> result = securityService.getAllSecurities();

        assertTrue(result.isEmpty());
        verify(securityRepository).findAll();
    }

    @Test
    void saveSecurities_shouldSaveAllSecurities() {
        List<Security> securities = List.of(
            Security.builder()
                .security_name("Security1")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.EQUITY)
                .build(),
            Security.builder()
                .security_name("Security2")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.DEBT)
                .build()
        );

        securityService.saveSecurities(securities);

        verify(securityRepository).saveAll(securities);
    }

    @Test
    void saveSecurities_emptyList_shouldCallRepository() {
        List<Security> securities = List.of();

        securityService.saveSecurities(securities);

        verify(securityRepository).saveAll(anyList());
    }

    @Test
    void deleteSecurityById_existingId_shouldReturnTrue() {
        Long id = 1L;
        Security security = Security.builder()
            .id(id)
            .security_name("Test Security")
            .security_type(SecurityType.FUND)
            .security_class(SecurityClass.EQUITY)
            .build();

        when(securityRepository.findById(id)).thenReturn(Optional.of(security));

        boolean result = securityService.deleteSecurityById(id);

        assertTrue(result);
        verify(securityRepository).findById(id);
        verify(securityRepository).delete(security);
    }

    @Test
    void deleteSecurityById_nonExistentId_shouldReturnFalse() {
        Long id = 999L;

        when(securityRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = securityService.deleteSecurityById(id);

        assertFalse(result);
        verify(securityRepository).findById(id);
        verify(securityRepository, never()).delete(any());
    }

    @Test
    void getSecurityNameMap_shouldReturnMapWithSecurityNames() {
        List<Security> securities = List.of(
            Security.builder()
                .id(1L)
                .security_name("Security1")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.EQUITY)
                .build(),
            Security.builder()
                .id(2L)
                .security_name("Security2")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.DEBT)
                .build()
        );

        when(securityRepository.findAll()).thenReturn(securities);

        Map<String, Security> result = securityService.getSecurityNameMap();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Security1"));
        assertTrue(result.containsKey("Security2"));
        assertEquals("Security1", result.get("Security1").getSecurity_name());
        assertEquals("Security2", result.get("Security2").getSecurity_name());
        verify(securityRepository).findAll();
    }

    @Test
    void getSecurityNameMap_emptyList_shouldReturnEmptyMap() {
        when(securityRepository.findAll()).thenReturn(List.of());

        Map<String, Security> result = securityService.getSecurityNameMap();

        assertTrue(result.isEmpty());
        verify(securityRepository).findAll();
    }

    @Test
    void getSecurityNameMap_shouldMapSecurityObjectsCorrectly() {
        Security security = Security.builder()
            .id(1L)
            .security_name("TestSecurity")
            .security_type(SecurityType.FUND)
            .security_class(SecurityClass.HYBRID)
            .build();

        when(securityRepository.findAll()).thenReturn(List.of(security));

        Map<String, Security> result = securityService.getSecurityNameMap();

        assertEquals(1, result.size());
        Security retrievedSecurity = result.get("TestSecurity");
        assertNotNull(retrievedSecurity);
        assertEquals(1L, retrievedSecurity.getId());
        assertEquals("TestSecurity", retrievedSecurity.getSecurity_name());
        assertEquals(SecurityType.FUND, retrievedSecurity.getSecurity_type());
        assertEquals(SecurityClass.HYBRID, retrievedSecurity.getSecurity_class());
    }
}
