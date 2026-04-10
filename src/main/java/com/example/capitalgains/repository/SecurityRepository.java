package com.example.capitalgains.repository;

import com.example.capitalgains.model.Security;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRepository extends JpaRepository<Security, Long> {
}
