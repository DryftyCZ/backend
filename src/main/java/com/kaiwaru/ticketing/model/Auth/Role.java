package com.kaiwaru.ticketing.model.Auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    private String name;

    public enum RoleName {
        VISITOR(1), WORKER(2), ORGANIZER(3), ADMIN(4);
        
        private final int priority;
        
        RoleName(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;
        }
    }
}

