package com.senibo.financetracker.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name")
    private AppRole roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JsonBackReference
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public Role(AppRole roleName) {
        this.roleName = roleName;
        this.users = new HashSet<>();
    }

}
