package com.kaiwaru.ticketing.service;

import com.kaiwaru.ticketing.dto.GeolocationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.CompletableFuture;

@Service
public class GeolocationService {
    private static final Logger logger = LoggerFactory.getLogger(GeolocationService.class);
    
    private final RestTemplate restTemplate;
    private static final String IP_API_URL = "http://ip-api.com/json/{ip}?fields=status,message,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,org,as,query";
    
    public GeolocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "geolocation", key = "#ipAddress")
    public GeolocationResponse getGeolocation(String ipAddress) {
        try {
            // Skip local/private IPs
            if (isLocalOrPrivateIP(ipAddress)) {
                return createLocalResponse(ipAddress);
            }

            logger.info("Fetching geolocation for IP: {}", ipAddress);
            GeolocationResponse response = restTemplate.getForObject(IP_API_URL, GeolocationResponse.class, ipAddress);
            
            if (response != null && response.isSuccess()) {
                logger.debug("Successfully retrieved geolocation for IP {}: {}, {}", 
                    ipAddress, response.getCountry(), response.getCity());
                return response;
            } else {
                logger.warn("Failed to get geolocation for IP {}: {}", ipAddress, 
                    response != null ? response.getStatus() : "null response");
                return createUnknownResponse(ipAddress);
            }
        } catch (RestClientException e) {
            logger.error("Error fetching geolocation for IP {}: {}", ipAddress, e.getMessage());
            return createUnknownResponse(ipAddress);
        }
    }

    @Cacheable(value = "geolocation", key = "#ipAddress")
    public CompletableFuture<GeolocationResponse> getGeolocationAsync(String ipAddress) {
        return CompletableFuture.supplyAsync(() -> getGeolocation(ipAddress));
    }

    private boolean isLocalOrPrivateIP(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return true;
        }
        
        // Common local/private IP patterns
        return ipAddress.equals("127.0.0.1") ||
               ipAddress.equals("::1") ||
               ipAddress.equals("0:0:0:0:0:0:0:1") ||
               ipAddress.startsWith("192.168.") ||
               ipAddress.startsWith("10.") ||
               ipAddress.startsWith("172.16.") ||
               ipAddress.startsWith("172.17.") ||
               ipAddress.startsWith("172.18.") ||
               ipAddress.startsWith("172.19.") ||
               ipAddress.startsWith("172.2") ||
               ipAddress.startsWith("172.30.") ||
               ipAddress.startsWith("172.31.") ||
               ipAddress.equals("localhost");
    }

    private GeolocationResponse createLocalResponse(String ipAddress) {
        GeolocationResponse response = new GeolocationResponse();
        response.setStatus("success");
        response.setCountry("Czech Republic");
        response.setCountryCode("CZ");
        response.setRegion("PR");
        response.setRegionName("Prague");
        response.setCity("Prague");
        response.setLatitude(50.0755);
        response.setLongitude(14.4378);
        response.setTimezone("Europe/Prague");
        response.setIsp("Local Network");
        response.setQuery(ipAddress);
        return response;
    }

    private GeolocationResponse createUnknownResponse(String ipAddress) {
        GeolocationResponse response = new GeolocationResponse();
        response.setStatus("success");
        response.setCountry("Unknown");
        response.setCountryCode("XX");
        response.setRegion("Unknown");
        response.setRegionName("Unknown");
        response.setCity("Unknown");
        response.setLatitude(0.0);
        response.setLongitude(0.0);
        response.setTimezone("UTC");
        response.setIsp("Unknown ISP");
        response.setQuery(ipAddress);
        return response;
    }

    public String getCountryFlag(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return "🏳️";
        }
        
        // Convert country code to flag emoji
        switch (countryCode.toUpperCase()) {
            case "CZ": return "🇨🇿";
            case "SK": return "🇸🇰";
            case "PL": return "🇵🇱";
            case "DE": return "🇩🇪";
            case "AT": return "🇦🇹";
            case "HU": return "🇭🇺";
            case "SI": return "🇸🇮";
            case "HR": return "🇭🇷";
            case "RO": return "🇷🇴";
            case "BG": return "🇧🇬";
            case "RS": return "🇷🇸";
            case "BA": return "🇧🇦";
            case "MK": return "🇲🇰";
            case "ME": return "🇲🇪";
            case "AL": return "🇦🇱";
            case "FR": return "🇫🇷";
            case "ES": return "🇪🇸";
            case "IT": return "🇮🇹";
            case "GB": return "🇬🇧";
            case "US": return "🇺🇸";
            case "CA": return "🇨🇦";
            case "RU": return "🇷🇺";
            case "UA": return "🇺🇦";
            case "BE": return "🇧🇪";
            case "NL": return "🇳🇱";
            case "CH": return "🇨🇭";
            case "DK": return "🇩🇰";
            case "SE": return "🇸🇪";
            case "NO": return "🇳🇴";
            case "FI": return "🇫🇮";
            case "EE": return "🇪🇪";
            case "LV": return "🇱🇻";
            case "LT": return "🇱🇹";
            default: return "🏳️";
        }
    }
}