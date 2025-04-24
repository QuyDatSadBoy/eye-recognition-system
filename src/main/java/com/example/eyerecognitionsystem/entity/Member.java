package com.example.eyerecognitionsystem.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblMember")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "phoneNumber")
    private Integer phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "department", length = 100)
    private String department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tblFullNameId")
    private FullName fullName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tblRoleId")
    private Role role;
}
