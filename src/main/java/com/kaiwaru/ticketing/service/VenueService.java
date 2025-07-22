package com.kaiwaru.ticketing.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kaiwaru.ticketing.dto.LocationSuggestion;

@Service
public class VenueService {
    
    private final List<LocationSuggestion> czechLocations = Arrays.asList(
        // Praha
        new LocationSuggestion("Praha", "Praha", "O2 Arena", "Českomoravská 2345/17, 190 00 Praha 9"),
        new LocationSuggestion("Praha", "Praha", "Národní divadlo", "Národní 2, 110 00 Praha 1"),
        new LocationSuggestion("Praha", "Praha", "Letní amfiteátr", "Křižíkova 34, 186 00 Praha 8"),
        new LocationSuggestion("Praha", "Praha", "Palác Akropolis", "Kubelíkova 1548/27, 130 00 Praha 3"),
        new LocationSuggestion("Praha", "Praha", "Stadion Letná", "Milady Horákové 1066/98, 170 00 Praha 7"),
        new LocationSuggestion("Praha", "Praha", "Kongresové centrum", "5. května 1640/65, 140 21 Praha 4"),
        
        // Brno
        new LocationSuggestion("Brno", "Brno", "Městské divadlo", "Dvořákova 11, 602 00 Brno"),
        new LocationSuggestion("Brno", "Brno", "Janáčkovo divadlo", "Rooseveltova 1, 602 00 Brno"),
        new LocationSuggestion("Brno", "Brno", "Stadion Za Lužánkami", "Sportovní 486/2, 602 00 Brno"),
        new LocationSuggestion("Brno", "Brno", "Velký sál Filharmonie", "Besední 3, 602 00 Brno"),
        new LocationSuggestion("Brno", "Brno", "Kuchařská škola", "Vídeňská 125, 639 00 Brno"),
        
        // Ostrava
        new LocationSuggestion("Ostrava", "Ostrava", "Nová scéna", "Českobratrská 1588/21, 702 00 Ostrava"),
        new LocationSuggestion("Ostrava", "Ostrava", "Bazaly", "Slovenská 2531/22, 709 00 Ostrava"),
        new LocationSuggestion("Ostrava", "Ostrava", "Dolní oblast Vítkovice", "Ruská 2993/37, 703 00 Ostrava"),
        
        // Plzeň
        new LocationSuggestion("Plzeň", "Plzeň", "Divadlo J. K. Tyla", "Smetanovy sady 16, 301 00 Plzeň"),
        new LocationSuggestion("Plzeň", "Plzeň", "Měštanská beseda", "Kopeckého sady 13, 301 00 Plzeň"),
        new LocationSuggestion("Plzeň", "Plzeň", "Doosan Arena", "Úslava 2, 301 00 Plzeň"),
        
        // České Budějovice
        new LocationSuggestion("České Budějovice", "České Budějovice", "Jihočeské divadlo", "Dr. Stejskala 19, 370 01 České Budějovice"),
        new LocationSuggestion("České Budějovice", "České Budějovice", "Budvar aréna", "Pražská 1247/16, 370 06 České Budějovice"),
        
        // Liberec
        new LocationSuggestion("Liberec", "Liberec", "F. X. Šaldy", "Husova 1634/8, 460 01 Liberec"),
        new LocationSuggestion("Liberec", "Liberec", "Bazén Liberec", "Masarykova 1404/32, 460 01 Liberec"),
        
        // Hradec Králové
        new LocationSuggestion("Hradec Králové", "Hradec Králové", "Klicperovo divadlo", "Dvořákova 58, 500 03 Hradec Králové"),
        new LocationSuggestion("Hradec Králové", "Hradec Králové", "Aldis aréna", "Sportovní 472, 500 09 Hradec Králové"),
        
        // Pardubice
        new LocationSuggestion("Pardubice", "Pardubice", "Východočeské divadlo", "Divadelní náměstí 64, 530 02 Pardubice"),
        new LocationSuggestion("Pardubice", "Pardubice", "Enteria Arena", "Ke Kamenci 2460, 530 02 Pardubice"),
        
        // Zlín
        new LocationSuggestion("Zlín", "Zlín", "Městské divadlo", "náměstí Míru 12, 760 01 Zlín"),
        new LocationSuggestion("Zlín", "Zlín", "Zimní stadion", "Okružní 4337, 760 05 Zlín"),
        
        // Olomouc
        new LocationSuggestion("Olomouc", "Olomouc", "Moravské divadlo", "Horní náměstí 22, 771 01 Olomouc"),
        new LocationSuggestion("Olomouc", "Olomouc", "Andrův stadion", "Hněvotínská 1031/7, 779 00 Olomouc"),
        
        // Jihlava
        new LocationSuggestion("Jihlava", "Jihlava", "Horácké divadlo", "Komenského 9, 586 01 Jihlava"),
        new LocationSuggestion("Jihlava", "Jihlava", "Městský stadion", "Tolstého 1, 586 01 Jihlava"),
        
        // Karlovy Vary
        new LocationSuggestion("Karlovy Vary", "Karlovy Vary", "Městské divadlo", "Divadelní náměstí 21, 360 01 Karlovy Vary"),
        new LocationSuggestion("Karlovy Vary", "Karlovy Vary", "KV Arena", "Olympic Center, 360 01 Karlovy Vary"),
        
        // Ústí nad Labem
        new LocationSuggestion("Ústí nad Labem", "Ústí nad Labem", "Severočeské divadlo", "Revoluční 1, 400 01 Ústí nad Labem"),
        new LocationSuggestion("Ústí nad Labem", "Ústí nad Labem", "Městský stadion", "Hraničářů 2934, 400 01 Ústí nad Labem")
    );
    
    public List<LocationSuggestion> searchLocations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        return czechLocations.stream()
            .filter(location -> 
                location.getCity().toLowerCase().contains(normalizedQuery) ||
                location.getVenueName().toLowerCase().contains(normalizedQuery) ||
                location.getAddress().toLowerCase().contains(normalizedQuery)
            )
            .limit(10)
            .collect(Collectors.toList());
    }
    
    public List<String> getCities() {
        return czechLocations.stream()
            .map(LocationSuggestion::getCity)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    public List<LocationSuggestion> getVenuesByCity(String city) {
        return czechLocations.stream()
            .filter(location -> location.getCity().equalsIgnoreCase(city))
            .collect(Collectors.toList());
    }
}