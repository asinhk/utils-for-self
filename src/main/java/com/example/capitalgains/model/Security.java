package com.example.capitalgains.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SECURITY")
public class Security {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String security_name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SecurityType security_type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SecurityClass security_class;
}
