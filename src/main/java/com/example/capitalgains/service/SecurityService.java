package com.example.capitalgains.service;

import com.example.capitalgains.model.Security;
import com.example.capitalgains.repository.SecurityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SecurityService {
    @Autowired
    private SecurityRepository securityRepository;

    @Cacheable(value = "Securities")
    public List<Security> getAllSecurities() {
        List<Security> allSecurities = securityRepository.findAll();
        log.info("{} securities retrieved", allSecurities.size());
        log.info(allSecurities.toString());
        return allSecurities;
    }

    public void saveSecurities(List<Security> securities) {
        securityRepository.saveAll(securities);
        log.info("{} securities saved successfully", securities.size());
    }

    public boolean deleteSecurityById(Long id) {
        Optional<Security> securityOptional = securityRepository.findById(id);
        if (securityOptional.isPresent()) {
            securityRepository.delete(securityOptional.get());
            log.info("Successfully deleted security with id {}", id);
            return true;
        } else {
            log.warn("Cannot delete security id {} as it does not exist", id);
            return false;
        }
    }


    public Map<String, Security> getSecurityNameMap() {
        return getAllSecurities()
                .stream()
                .collect(Collectors.toMap(Security::getSecurity_name, Function.identity()));
    }

}
