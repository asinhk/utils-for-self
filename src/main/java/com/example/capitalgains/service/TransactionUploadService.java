package com.example.capitalgains.service;

import com.example.capitalgains.model.BuySell;
import com.example.capitalgains.model.CreateTransactionDTO;
import com.example.capitalgains.model.Security;
import com.example.capitalgains.model.Transaction;
import com.example.capitalgains.util.ModelMapperHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TransactionUploadService {

    @Autowired
    TransactionService transactionService;

    @Autowired
    SecurityService securityService;

    public void uploadTransactionsFromFile(MultipartFile file) {
        Map<String, Security> securityMap = securityService.getSecurityNameMap();
        List<Transaction> transactions = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet worksheet = workbook.getSheetAt(1);
            for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                Row row = worksheet.getRow(index);
                CreateTransactionDTO createTransactionDTO = createTransactionDTOFromRow(row, securityMap);
                Transaction transaction = ModelMapperHolder.getModelMapper().map(createTransactionDTO, Transaction.class);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        transactionService.saveTransactions(transactions);
    }

    public CreateTransactionDTO createTransactionDTOFromRow(Row row, Map<String, Security> securityMap) {
        Date transactiondt = row.getCell(0).getDateCellValue();
        LocalDate transaction_dt = LocalDate.ofInstant(transactiondt.toInstant(), ZoneId.systemDefault());
        String security_name = row.getCell(1).getStringCellValue();
        BuySell buy_sell = BuySell.getFromString((row.getCell(2).getStringCellValue()));
        Double price = Double.valueOf(row.getCell(3).getNumericCellValue());
        Double units = Double.valueOf(row.getCell(4).getNumericCellValue());
        Long security_id = securityMap.get(security_name).getId();
        return CreateTransactionDTO.builder().transaction_dt(transaction_dt).buy_sell(buy_sell).price(price).security_name(security_name).security_id(security_id)
                .units(units).active(1).build();
    }


}
