package com.example.capitalgains.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionDTO {
    Long security_id;

    String security_name;

    LocalDate transaction_dt;

    BuySell buy_sell;

    Double price;

    Double units;

    int active;
}
