package com.example.capitalgains.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSecurityDTO {
    private String security_name;

    private SecurityType security_type;

    private SecurityClass security_class;
}
