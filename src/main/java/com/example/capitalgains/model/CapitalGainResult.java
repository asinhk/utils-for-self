package com.example.capitalgains.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CapitalGainResult {

   private Double withdrawalAmount;
   List<CapitalGain> capitalGainsPerSecurity;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapitalGain{
        private String securityName;
        private Double capitalGains;
    }
}
