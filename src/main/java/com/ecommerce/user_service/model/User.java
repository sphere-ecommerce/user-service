package com.ecommerce.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;
    private String email;
    private String password;
}
