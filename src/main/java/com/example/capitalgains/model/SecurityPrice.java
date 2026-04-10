package com.example.capitalgains.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SECURITY_PRICE")
public class SecurityPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long security_id;
    @Column(nullable = false)
    private String security_name;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private LocalDate update_dt;

}
