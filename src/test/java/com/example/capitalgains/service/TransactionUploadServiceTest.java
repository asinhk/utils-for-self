package com.example.capitalgains.service;

import com.example.capitalgains.model.BuySell;
import com.example.capitalgains.model.CreateTransactionDTO;
import com.example.capitalgains.model.Security;
import com.example.capitalgains.model.Transaction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionUploadServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TransactionUploadService transactionUploadService;

    private Map<String, Security> securityMap;

    @BeforeEach
    void setUp() {
        securityMap = Map.of(
            "Security1", Security.builder().id(1L).security_name("Security1").build(),
            "Security2", Security.builder().id(2L).security_name("Security2").build()
        );
    }

    @Test
    void uploadTransactionsFromFile_shouldUploadSuccessfully() throws IOException {
        MultipartFile file = createTestExcelFile(2);

        when(securityService.getSecurityNameMap()).thenReturn(securityMap);
        doNothing().when(transactionService).saveTransactions(anyList());

        transactionUploadService.uploadTransactionsFromFile(file);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).saveTransactions(captor.capture());

        List<Transaction> savedTransactions = captor.getValue();
        assertEquals(2, savedTransactions.size());
    }

    @Test
    void uploadTransactionsFromFile_singleTransaction_shouldSave() throws IOException {
        MultipartFile file = createTestExcelFile(1);

        when(securityService.getSecurityNameMap()).thenReturn(securityMap);
        doNothing().when(transactionService).saveTransactions(anyList());

        transactionUploadService.uploadTransactionsFromFile(file);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).saveTransactions(captor.capture());

        List<Transaction> savedTransactions = captor.getValue();
        assertEquals(1, savedTransactions.size());
    }

    @Test
    void createTransactionDTOFromRow_shouldMapRowToDTO() throws IOException {
        Row row = createTestRow();

        CreateTransactionDTO dto = transactionUploadService.createTransactionDTOFromRow(row, securityMap);

        assertNotNull(dto);
        assertEquals(1L, dto.getSecurity_id());
        assertEquals("Security1", dto.getSecurity_name());
        assertEquals(BuySell.B, dto.getBuy_sell());
        assertEquals(100.0, dto.getPrice());
        assertEquals(10.0, dto.getUnits());
        assertEquals(1, dto.getActive());
    }

    @Test
    void createTransactionDTOFromRow_buyTransaction_shouldMapCorrectly() throws IOException {
        Row row = createTestRowWithBuySell("Buy");

        CreateTransactionDTO dto = transactionUploadService.createTransactionDTOFromRow(row, securityMap);

        assertEquals(BuySell.B, dto.getBuy_sell());
        assertEquals(100.0, dto.getPrice());
        assertEquals(10.0, dto.getUnits());
    }

    @Test
    void createTransactionDTOFromRow_sellTransaction_shouldMapCorrectly() throws IOException {
        Row row = createTestRowWithBuySell("Sell");

        CreateTransactionDTO dto = transactionUploadService.createTransactionDTOFromRow(row, securityMap);

        assertEquals(BuySell.S, dto.getBuy_sell());
    }

    @Test
    void createTransactionDTOFromRow_withSecurity2_shouldMapToCorrectId() throws IOException {
        Row row = createTestRowWithSecurity("Security2");

        CreateTransactionDTO dto = transactionUploadService.createTransactionDTOFromRow(row, securityMap);

        assertEquals(2L, dto.getSecurity_id());
        assertEquals("Security2", dto.getSecurity_name());
    }

    @Test
    void uploadTransactionsFromFile_multipleTransactions_shouldPreserveData() throws IOException {
        MultipartFile file = createTestExcelFile(3);

        when(securityService.getSecurityNameMap()).thenReturn(securityMap);
        doNothing().when(transactionService).saveTransactions(anyList());

        transactionUploadService.uploadTransactionsFromFile(file);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).saveTransactions(captor.capture());

        List<Transaction> savedTransactions = captor.getValue();
        assertEquals(3, savedTransactions.size());
        assertTrue(savedTransactions.stream().allMatch(t -> t.getActive() == 1));
    }

    @Test
    void uploadTransactionsFromFile_invalidFile_shouldThrowRuntimeException() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "invalid content".getBytes());

        when(securityService.getSecurityNameMap()).thenReturn(securityMap);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            transactionUploadService.uploadTransactionsFromFile(file));

        assertNotNull(exception);
    }

    @Test
    void createTransactionDTOFromRow_shouldHaveCorrectTransactionDate() throws IOException {
        Row row = createTestRow();

        CreateTransactionDTO dto = transactionUploadService.createTransactionDTOFromRow(row, securityMap);

        assertNotNull(dto.getTransaction_dt());
        assertTrue(dto.getTransaction_dt().isBefore(LocalDate.now().plusDays(1)));
    }

    @Test
    void uploadTransactionsFromFile_callsSecurityServiceOnce() throws IOException {
        MultipartFile file = createTestExcelFile(2);

        when(securityService.getSecurityNameMap()).thenReturn(securityMap);
        doNothing().when(transactionService).saveTransactions(anyList());

        transactionUploadService.uploadTransactionsFromFile(file);

        verify(securityService).getSecurityNameMap();
    }

    // Helper methods to create test data
    private MultipartFile createTestExcelFile(int numRows) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.createSheet();
            Sheet dataSheet = workbook.createSheet();

            // Create header row
            Row headerRow = dataSheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Security");
            headerRow.createCell(2).setCellValue("BuySell");
            headerRow.createCell(3).setCellValue("Price");
            headerRow.createCell(4).setCellValue("Units");

            // Create data rows
            for (int i = 1; i <= numRows; i++) {
                Row row = dataSheet.createRow(i);
                row.createCell(0).setCellValue(new Date());
                row.createCell(1).setCellValue("Security1");
                row.createCell(2).setCellValue("Buy");
                row.createCell(3).setCellValue(100.0);
                row.createCell(4).setCellValue(10.0);
            }

            workbook.write(output);

            return new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", output.toByteArray());
        }
    }

    private Row createTestRow() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue(new Date());
            row.createCell(1).setCellValue("Security1");
            row.createCell(2).setCellValue("Buy");
            row.createCell(3).setCellValue(100.0);
            row.createCell(4).setCellValue(10.0);

            return row;
        }
    }

    private Row createTestRowWithBuySell(String buySell) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue(new Date());
            row.createCell(1).setCellValue("Security1");
            row.createCell(2).setCellValue(buySell);
            row.createCell(3).setCellValue(100.0);
            row.createCell(4).setCellValue(10.0);

            return row;
        }
    }

    @SuppressWarnings("UnusedParameter")
    private Row createTestRowWithSecurity(String securityName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue(new Date());
            row.createCell(1).setCellValue(securityName);
            row.createCell(2).setCellValue("Buy");
            row.createCell(3).setCellValue(100.0);
            row.createCell(4).setCellValue(10.0);

            return row;
        }
    }
}
