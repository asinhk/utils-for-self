package com.example.capitalgains.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    Long security_id;

    @Column(nullable = false)
    String security_name;

    @Column(nullable = false)
    LocalDate transaction_dt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    BuySell buy_sell;

    @Column(nullable = false)
    Double price;

    @Column(nullable = false)
    Double units;

    @Column(nullable = false )
    @ColumnDefault("1")
    int active;
}
