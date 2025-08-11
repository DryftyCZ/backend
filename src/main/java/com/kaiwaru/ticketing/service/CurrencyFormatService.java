package com.kaiwaru.ticketing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kaiwaru.ticketing.model.OrganizationSettings;
import com.kaiwaru.ticketing.repository.OrganizationSettingsRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@Service
public class CurrencyFormatService {
    
    @Autowired
    private OrganizationSettingsRepository settingsRepository;
    
    private final Map<String, String> currencySymbols = new HashMap<String, String>() {{
        put("CZK", "Kč");
        put("EUR", "€");
        put("USD", "$");
        put("GBP", "£");
        put("PLN", "zł");
    }};
    
    public String formatCurrency(BigDecimal amount) {
        OrganizationSettings settings = getOrganizationSettings();
        String currency = settings.getCurrency();
        String symbol = currencySymbols.getOrDefault(currency, currency);
        
        if ("EUR".equals(currency) || "USD".equals(currency) || "GBP".equals(currency)) {
            return symbol + amount.toString();
        } else {
            return amount.toString() + " " + symbol;
        }
    }
    
    public String getCurrencyCode() {
        OrganizationSettings settings = getOrganizationSettings();
        return settings.getCurrency();
    }
    
    public String getCurrencySymbol() {
        String currency = getCurrencyCode();
        return currencySymbols.getOrDefault(currency, currency);
    }
    
    private OrganizationSettings getOrganizationSettings() {
        return settingsRepository.findAll().stream()
                .findFirst()
                .orElse(new OrganizationSettings());
    }
}