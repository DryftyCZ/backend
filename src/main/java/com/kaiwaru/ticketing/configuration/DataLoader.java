package com.kaiwaru.ticketing.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kaiwaru.ticketing.model.Auth.Role;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.RoleRepository;
import com.kaiwaru.ticketing.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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
}