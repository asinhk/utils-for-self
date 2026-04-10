package com.example.capitalgains.service;

import com.example.capitalgains.model.CreateSecurityPriceDTO;
import com.example.capitalgains.model.Security;
import com.example.capitalgains.model.SecurityPrice;
import com.example.capitalgains.repository.SecurityPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityPriceServiceTest {

    @Mock
    private SecurityPriceRepository securityPriceRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private SecurityPriceService securityPriceService;

    @BeforeEach
    void setUp() {
        // No setup needed as mocks are handled by MockitoExtension
    }

    @Test
    void saveAllSecurityPrices_shouldSaveMappedPrices() {
        List<CreateSecurityPriceDTO> dtos = List.of(
                CreateSecurityPriceDTO.builder()
                        .security_name("Security1")
                        .price(100.0)
                        .build(),
                CreateSecurityPriceDTO.builder()
                        .security_name("Security2")
                        .price(200.0)
                        .build()
        );

        Map<String, Security> securityMap = Map.of(
                "Security1", Security.builder().id(1L).security_name("Security1").build(),
                "Security2", Security.builder().id(2L).security_name("Security2").build()
        );

        when(securityService.getSecurityNameMap()).thenReturn(securityMap);

        securityPriceService.saveAllSecurityPrices(dtos);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SecurityPrice>> captor = ArgumentCaptor.forClass(List.class);
        verify(securityPriceRepository).saveAll(captor.capture());

        List<SecurityPrice> savedPrices = captor.getValue();
        assertEquals(2, savedPrices.size());

        SecurityPrice price1 = savedPrices.stream().filter(p -> p.getSecurity_name().equals("Security1")).findFirst().orElse(null);
        assertNotNull(price1);
        assertEquals(1L, price1.getSecurity_id());
        assertEquals("Security1", price1.getSecurity_name());
        assertEquals(100.0, price1.getPrice());
        assertEquals(LocalDate.now(), price1.getUpdate_dt());

        SecurityPrice price2 = savedPrices.stream().filter(p -> p.getSecurity_name().equals("Security2")).findFirst().orElse(null);
        assertNotNull(price2);
        assertEquals(2L, price2.getSecurity_id());
        assertEquals("Security2", price2.getSecurity_name());
        assertEquals(200.0, price2.getPrice());
        assertEquals(LocalDate.now(), price2.getUpdate_dt());
    }

    @Test
    void getAllSecurityPrices_shouldReturnAllPrices() {
        List<SecurityPrice> expectedPrices = List.of(
                SecurityPrice.builder()
                        .id(1L)
                        .security_id(1L)
                        .security_name("Security1")
                        .price(100.0)
                        .update_dt(LocalDate.now())
                        .build(),
                SecurityPrice.builder()
                        .id(2L)
                        .security_id(2L)
                        .security_name("Security2")
                        .price(200.0)
                        .update_dt(LocalDate.now())
                        .build()
        );

        when(securityPriceRepository.findAll()).thenReturn(expectedPrices);

        List<SecurityPrice> result = securityPriceService.getAllSecurityPrices();

        assertEquals(expectedPrices, result);
        verify(securityPriceRepository).findAll();
    }

    @Test
    void saveAllSecurityPrices_emptyList_shouldSaveEmptyList() {
        List<CreateSecurityPriceDTO> dtos = List.of();

        securityPriceService.saveAllSecurityPrices(dtos);

        verify(securityPriceRepository).saveAll(anyList());
        verify(securityService, never()).getSecurityNameMap();
    }
}
