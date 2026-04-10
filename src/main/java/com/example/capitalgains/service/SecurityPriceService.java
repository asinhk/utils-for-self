package com.example.capitalgains.service;

import com.example.capitalgains.model.CreateSecurityPriceDTO;
import com.example.capitalgains.model.Security;
import com.example.capitalgains.model.SecurityPrice;
import com.example.capitalgains.repository.SecurityPriceRepository;
import com.example.capitalgains.util.ModelMapperHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SecurityPriceService {
    @Autowired
    private SecurityPriceRepository securityPriceRepository;
    @Autowired
    private SecurityService securityService;

    public void saveAllSecurityPrices(List<CreateSecurityPriceDTO> createSecurityPriceDTOList) {
        log.info("Saving all security prices to db");
        securityPriceRepository.saveAll(createSecurityPriceDTOList.stream().map(this::mapDtoToSecurityPrice).collect(Collectors.toList()));
    }

    @Cacheable(value="SecurityPrices")
    public List<SecurityPrice> getAllSecurityPrices() {
        log.info("Fetching all security prices from db");
        return securityPriceRepository.findAll();
    }

    private SecurityPrice mapDtoToSecurityPrice(CreateSecurityPriceDTO createSecurityPriceDTO) {
        SecurityPrice securityPrice = ModelMapperHolder.getModelMapper().map(createSecurityPriceDTO, SecurityPrice.class);
        Map<String, Security> securityMap = securityService.getSecurityNameMap();
        securityPrice.setSecurity_id(securityMap.get(createSecurityPriceDTO.getSecurity_name()).getId());
        securityPrice.setUpdate_dt(LocalDate.now());
        return securityPrice;
    }


}
