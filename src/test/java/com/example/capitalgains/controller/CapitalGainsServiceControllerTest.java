package com.example.capitalgains.controller;

import com.example.capitalgains.model.*;
import com.example.capitalgains.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CapitalGainsServiceController.class)
class CapitalGainsServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SecurityService securityService;

    @Mock
    private TransactionUploadService transactionUploadService;

    @Mock
    private SecurityPriceService securityPriceService;

    @Mock
    private CapitalGainsService capitalGainsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTransactions_shouldReturnListOfTransactions() throws Exception {
        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .id(1L)
                        .security_id(1L)
                        .security_name("Test Security")
                        .transaction_dt(LocalDate.now())
                        .buy_sell(BuySell.B)
                        .price(100.0)
                        .units(10.0)
                        .active(1)
                        .build()
        );

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/capitalgainsservice/getAllTransactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].security_name").value("Test Security"));

        verify(transactionService).getAllTransactions();
    }

    @Test
    void saveTransaction_shouldSaveTransaction() throws Exception {
        CreateTransactionDTO dto = CreateTransactionDTO.builder()
                .security_id(1L)
                .security_name("Test Security")
                .transaction_dt(LocalDate.now())
                .buy_sell(BuySell.B)
                .price(100.0)
                .units(10.0)
                .active(1)
                .build();

        doNothing().when(transactionService).saveTransactions(any());

        mockMvc.perform(post("/capitalgainsservice/saveTransaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(transactionService).saveTransactions(any());
    }

    @Test
    void deleteTransactionById_shouldDeleteTransaction() throws Exception {
        when(transactionService.deleteTransactionById(1L)).thenReturn(true);

        mockMvc.perform(delete("/capitalgainsservice/deleteTransaction/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(transactionService).deleteTransactionById(1L);
    }

    @Test
    void getAllSecurities_shouldReturnListOfSecurities() throws Exception {
        List<Security> securities = List.of(
                Security.builder()
                        .id(1L)
                        .security_name("Test Security")
                        .security_type(SecurityType.FUND)
                        .security_class(SecurityClass.DEBT)
                        .build()
        );

        when(securityService.getAllSecurities()).thenReturn(securities);

        mockMvc.perform(get("/capitalgainsservice/getAllSecurities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].security_name").value("Test Security"));

        verify(securityService).getAllSecurities();
    }

    @Test
    void saveSecurity_shouldSaveSecurity() throws Exception {
        CreateSecurityDTO dto = CreateSecurityDTO.builder()
                .security_name("Test Security")
                .security_type(SecurityType.FUND)
                .security_class(SecurityClass.DEBT)
                .build();

        doNothing().when(securityService).saveSecurities(any());

        mockMvc.perform(post("/capitalgainsservice/saveSecurity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(securityService).saveSecurities(any());
    }

    @Test
    void deleteSecurityById_shouldDeleteSecurity() throws Exception {
        when(securityService.deleteSecurityById(1L)).thenReturn(true);

        mockMvc.perform(delete("/capitalgainsservice/deleteSecurity/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(securityService).deleteSecurityById(1L);
    }

    @Test
    void uploadTransactionsFromFile_shouldUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test data".getBytes());

        doNothing().when(transactionUploadService).uploadTransactionsFromFile(any());

        mockMvc.perform(multipart("/capitalgainsservice/upload-transactions-excel").file(file))
                .andExpect(status().isOk());

        verify(transactionUploadService).uploadTransactionsFromFile(any());
    }

    @Test
    void getAllSecurityPrices_shouldReturnListOfPrices() throws Exception {
        List<SecurityPrice> prices = List.of(
                SecurityPrice.builder()
                        .id(1L)
                        .security_id(1L)
                        .security_name("Test Security")
                        .price(100.0)
                        .update_dt(LocalDate.now())
                        .build()
        );

        when(securityPriceService.getAllSecurityPrices()).thenReturn(prices);

        mockMvc.perform(get("/capitalgainsservice/getAllPrices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].price").value(100.0));

        verify(securityPriceService).getAllSecurityPrices();
    }

    @Test
    void saveAllSecurityPrices_shouldSavePrices() throws Exception {
        List<CreateSecurityPriceDTO> dtos = List.of(
                CreateSecurityPriceDTO.builder()
                        .security_name("Test Security")
                        .price(100.0)
                        .build()
        );

        doNothing().when(securityPriceService).saveAllSecurityPrices(any());

        mockMvc.perform(post("/capitalgainsservice/saveAllPrices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isOk());

        verify(securityPriceService).saveAllSecurityPrices(any());
    }

    @Test
    void getCapitalGainsForAmount_shouldReturnCapitalGainResult() throws Exception {
        CapitalGainResult result = CapitalGainResult.builder()
                .withdrawalAmount(1000.0)
                .capitalGainsPerSecurity(List.of(
                        new CapitalGainResult.CapitalGain("Test Security", 100.0)
                ))
                .build();

        when(capitalGainsService.getCapitalGainsForWithdrawalAmount(1000.0)).thenReturn(result);

        mockMvc.perform(get("/capitalgainsservice/getCapitalGainsForAmount")
                        .param("withdrawalAmount", "1000.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.withdrawalAmount").value(1000.0))
                .andExpect(jsonPath("$.capitalGainsPerSecurity[0].securityName").value("Test Security"))
                .andExpect(jsonPath("$.capitalGainsPerSecurity[0].capitalGains").value(100.0));

        verify(capitalGainsService).getCapitalGainsForWithdrawalAmount(1000.0);
    }
}
