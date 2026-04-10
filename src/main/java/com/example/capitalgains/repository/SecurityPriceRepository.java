package com.example.capitalgains.repository;

import com.example.capitalgains.model.SecurityPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityPriceRepository extends JpaRepository<SecurityPrice, Long> {
}
