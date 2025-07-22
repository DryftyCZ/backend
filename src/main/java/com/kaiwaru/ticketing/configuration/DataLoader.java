package com.kaiwaru.ticketing.configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kaiwaru.ticketing.model.Auth.Role;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.repository.RoleRepository;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.TicketTypeRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${app.default-admin-password}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        createRoleIfNotExists(Role.RoleName.VISITOR.name());
        createRoleIfNotExists(Role.RoleName.WORKER.name());
        createRoleIfNotExists(Role.RoleName.ORGANIZER.name());
        createRoleIfNotExists(Role.RoleName.ADMIN.name());

        // Create default admin user if it doesn't exist
        createDefaultAdminUser();
        
        // Create dummy test users
        createDummyUsers();
        
        // Create dummy events with ticket types and tickets
        createDummyEvents();
        createDummyTicketTypes();
        createDummyTickets();
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            log.info("Created role: " + roleName);
        }
    }

    private void createDefaultAdminUser() {
        String adminUsername = "admin";
        String adminEmail = "admin@ticketing.com";
        
        if (!userRepository.existsByUsername(adminUsername) && !userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode(defaultAdminPassword));
            
            Set<Role> adminRoles = new HashSet<>();
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("Error: Admin role not found"));
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            
            userRepository.save(admin);
            log.info("Created default admin user: " + adminUsername);
            log.info("Please change the default password!");
        }
    }
    
    private void createDummyUsers() {
        // Create organizer user
        createUserIfNotExists("organizer", "organizer@example.com", "password123", Role.RoleName.ORGANIZER);
        
        // Create worker user
        createUserIfNotExists("worker", "worker@example.com", "password123", Role.RoleName.WORKER);
        
        // Create visitor user
        createUserIfNotExists("visitor", "visitor@example.com", "password123", Role.RoleName.VISITOR);
        
        // Create test user
        createUserIfNotExists("test", "test@example.com", "test123", Role.RoleName.VISITOR);
    }
    
    private void createUserIfNotExists(String username, String email, String password, Role.RoleName roleName) {
        if (!userRepository.existsByUsername(username) && !userRepository.existsByEmail(email)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(encoder.encode(password));
            
            Set<Role> roles = new HashSet<>();
            Role role = roleRepository.findByName(roleName.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " not found"));
            roles.add(role);
            user.setRoles(roles);
            
            userRepository.save(user);
            log.info("Created dummy user: " + username + " with role: " + roleName);
        }
    }
    
    private void createDummyEvents() {
        // Create events only if none exist
        if (eventRepository.count() == 0) {
            
            // Get organizer and admin users for assigning to events
            User adminUser = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            User organizerUser = userRepository.findByUsername("organizer")
                    .orElse(adminUser); // Fallback to admin if organizer doesn't exist
            
            // Event 1: Concert (už proběhl) - assigned to organizer
            Event concert = new Event();
            concert.setName("Summer Rock Festival 2024");
            concert.setDescription("Největší rockový festival roku s nejlepšími českými a zahraničními kapelami. Třídenní zážitek plný hudby, zábavy a skvělé atmosféry.");
            concert.setAddress("Letní amfiteátr, Křižíkova 34, 186 00 Praha 8");
            concert.setCity("Praha");
            concert.setDate(LocalDate.of(2024, 8, 15));
            concert.setOrganizer(organizerUser);
            eventRepository.save(concert);
            
            // Event 2: Conference (už proběhl) - assigned to admin
            Event conference = new Event();
            conference.setName("Tech Conference Prague 2024");
            conference.setDescription("Mezinárodní konference o nejnovějších technologiích. Přednášky od expertů z celého světa, networking a workshopy.");
            conference.setAddress("Kongresové centrum, 5. května 1640/65, 140 21 Praha 4");
            conference.setCity("Praha");
            conference.setDate(LocalDate.of(2024, 9, 20));
            conference.setOrganizer(adminUser);
            eventRepository.save(conference);
            
            // Event 3: Theater (už proběhl) - assigned to organizer
            Event theater = new Event();
            theater.setName("Romeo a Julie");
            theater.setDescription("Klasická shakespearovská tragédie v moderní režii. Mimořádné představení s hvězdným obsazením.");
            theater.setAddress("Národní divadlo, Národní 2, 110 00 Praha 1");
            theater.setCity("Praha");
            theater.setDate(LocalDate.of(2024, 10, 5));
            theater.setOrganizer(organizerUser);
            eventRepository.save(theater);
            
            // Event 4: Sports Event (už proběhl) - assigned to admin
            Event sports = new Event();
            sports.setName("Fotbalový zápas: Sparta vs Slavia");
            sports.setDescription("Derby hlavního města! Nejočekávanější zápas sezóny mezi tradičními rivaly.");
            sports.setAddress("Stadion Letná, Milady Horákové 1066/98, 170 00 Praha 7");
            sports.setCity("Praha");
            sports.setDate(LocalDate.of(2024, 11, 10));
            sports.setOrganizer(adminUser);
            eventRepository.save(sports);
            
            // Event 5: Workshop (plánovaný) - assigned to organizer
            Event workshop = new Event();
            workshop.setName("Kurz vaření italské kuchyně");
            workshop.setDescription("Praktický kurz vaření pod vedením italského šéfkuchaře. Naučíte se připravit autentické italské pokrmy.");
            workshop.setAddress("Kuchařská škola, Vídeňská 125, 639 00 Brno");
            workshop.setCity("Brno");
            workshop.setDate(LocalDate.of(2025, 3, 15));
            workshop.setOrganizer(organizerUser);
            eventRepository.save(workshop);
            
            log.info("Created 5 dummy events");
        }
    }
    
    private void createDummyTicketTypes() {
        // Create ticket types only if none exist
        if (ticketTypeRepository.count() == 0) {
            
            var events = eventRepository.findAll();
            
            for (Event event : events) {
                // Standard ticket type
                TicketType standardType = new TicketType();
                standardType.setName("Standard");
                standardType.setDescription("Standardní vstupenka");
                standardType.setPrice(new BigDecimal("500.00"));
                standardType.setQuantity(100);
                standardType.setAvailableQuantity(100);
                standardType.setEvent(event);
                ticketTypeRepository.save(standardType);
                
                // VIP ticket type
                TicketType vipType = new TicketType();
                vipType.setName("VIP");
                vipType.setDescription("VIP vstupenka s prémiovou obsluhou");
                vipType.setPrice(new BigDecimal("1200.00"));
                vipType.setQuantity(50);
                vipType.setAvailableQuantity(50);
                vipType.setEvent(event);
                ticketTypeRepository.save(vipType);
                
                // Different ticket types for different events
                if (event.getName().contains("Rock Festival")) {
                    TicketType backstageType = new TicketType();
                    backstageType.setName("Backstage");
                    backstageType.setDescription("Exkluzivní přístup do zákulisí");
                    backstageType.setPrice(new BigDecimal("2500.00"));
                    backstageType.setQuantity(20);
                    backstageType.setAvailableQuantity(20);
                    backstageType.setEvent(event);
                    ticketTypeRepository.save(backstageType);
                } else if (event.getName().contains("Romeo")) {
                    TicketType balconyType = new TicketType();
                    balconyType.setName("Balkon");
                    balconyType.setDescription("Místa na balkoně s výborným výhledem");
                    balconyType.setPrice(new BigDecimal("800.00"));
                    balconyType.setQuantity(30);
                    balconyType.setAvailableQuantity(30);
                    balconyType.setEvent(event);
                    ticketTypeRepository.save(balconyType);
                } else if (event.getName().contains("Sparta")) {
                    TicketType tribuneType = new TicketType();
                    tribuneType.setName("Tribuna");
                    tribuneType.setDescription("Krytá tribuna s dobrým výhledem");
                    tribuneType.setPrice(new BigDecimal("700.00"));
                    tribuneType.setQuantity(200);
                    tribuneType.setAvailableQuantity(200);
                    tribuneType.setEvent(event);
                    ticketTypeRepository.save(tribuneType);
                }
            }
            
            log.info("Created dummy ticket types for all events");
        }
    }
    
    private void createDummyTickets() {
        // Create tickets only if none exist
        if (ticketRepository.count() == 0) {
            
            // Get all events, users, and ticket types
            var events = eventRepository.findAll();
            var users = userRepository.findAll();
            var ticketTypes = ticketTypeRepository.findAll();
            
            if (!events.isEmpty() && !users.isEmpty() && !ticketTypes.isEmpty()) {
                
                int ticketCounter = 1;
                
                // Create tickets for each event
                for (Event event : events) {
                    var eventTicketTypes = ticketTypes.stream()
                            .filter(tt -> tt.getEvent().getId().equals(event.getId()))
                            .toList();
                    
                    for (TicketType ticketType : eventTicketTypes) {
                        // Create more used tickets for realistic sales data
                        int usedTicketsCount = Math.min(15, ticketType.getQuantity() / 2);
                        for (int i = 0; i < usedTicketsCount; i++) {
                            Ticket usedTicket = new Ticket();
                            usedTicket.setQrCode(UUID.randomUUID().toString());
                            usedTicket.setUsed(true);
                            usedTicket.setTicketNumber("T" + event.getId() + "-" + String.format("%03d", ticketCounter++));
                            usedTicket.setCustomerEmail("customer" + ticketCounter + "@example.com");
                            usedTicket.setCustomerName("Jan Novák " + ticketCounter);
                            // Spread purchase dates over period before event date
                            LocalDate eventDate = event.getDate();
                            int daysBeforeEvent = 30 + (ticketCounter % 60); // 30-90 days before event
                            usedTicket.setPurchaseDate(eventDate.minusDays(daysBeforeEvent).atStartOfDay());
                            usedTicket.setUsedDate(eventDate.atStartOfDay());
                            // Add geographic diversity for realistic analytics
                            String[] cities = {"Praha", "Brno", "Ostrava", "Plzeň", "Olomouc", "České Budějovice"};
                            String[] countries = {"Czech Republic", "Slovakia", "Austria", "Germany", "Poland"};
                            int geoIndex = ticketCounter % cities.length;
                            
                            usedTicket.setCountry(geoIndex < 4 ? "Czech Republic" : countries[geoIndex % countries.length]);
                            usedTicket.setCity(cities[geoIndex]);
                            usedTicket.setIpAddress("192.168." + ((geoIndex % 3) + 1) + "." + (100 + (ticketCounter % 50)));
                            usedTicket.setEvent(event);
                            usedTicket.setTicketType(ticketType);
                            
                            // Assign to a user if available
                            if (users.size() > (ticketCounter % users.size())) {
                                usedTicket.setCustomer(users.get(ticketCounter % users.size()));
                            }
                            
                            ticketRepository.save(usedTicket);
                            
                            // Decrease available quantity
                            ticketType.decreaseAvailableQuantity();
                        }
                        
                        // Create more unused tickets for realistic sales data
                        int unusedTicketsCount = Math.min(10, ticketType.getQuantity() / 3);
                        for (int i = 0; i < unusedTicketsCount; i++) {
                            Ticket unusedTicket = new Ticket();
                            unusedTicket.setQrCode(UUID.randomUUID().toString());
                            unusedTicket.setUsed(false);
                            unusedTicket.setTicketNumber("T" + event.getId() + "-" + String.format("%03d", ticketCounter++));
                            unusedTicket.setCustomerEmail("customer" + ticketCounter + "@example.com");
                            unusedTicket.setCustomerName("Marie Svobodová " + ticketCounter);
                            // Spread purchase dates over period before event date
                            LocalDate eventDate = event.getDate();
                            int daysBeforeEvent = 30 + (ticketCounter % 60); // 30-90 days before event
                            unusedTicket.setPurchaseDate(eventDate.minusDays(daysBeforeEvent).atStartOfDay());
                            // Add geographic diversity for realistic analytics
                            String[] cities = {"Praha", "Brno", "Ostrava", "Plzeň", "Olomouc", "České Budějovice"};
                            String[] countries = {"Czech Republic", "Slovakia", "Austria", "Germany", "Poland"};
                            int geoIndex = ticketCounter % cities.length;
                            
                            unusedTicket.setCountry(geoIndex < 4 ? "Czech Republic" : countries[geoIndex % countries.length]);
                            unusedTicket.setCity(cities[geoIndex]);
                            unusedTicket.setIpAddress("192.168." + ((geoIndex % 3) + 1) + "." + (200 + (ticketCounter % 50)));
                            unusedTicket.setEvent(event);
                            unusedTicket.setTicketType(ticketType);
                            
                            // Assign to a user if available
                            if (users.size() > (ticketCounter % users.size())) {
                                unusedTicket.setCustomer(users.get(ticketCounter % users.size()));
                            }
                            
                            ticketRepository.save(unusedTicket);
                            
                            // Decrease available quantity
                            ticketType.decreaseAvailableQuantity();
                        }
                        
                        // Save updated ticket type
                        ticketTypeRepository.save(ticketType);
                    }
                }
                
                log.info("Created dummy tickets for all events with ticket types");
            }
        }
    }
}