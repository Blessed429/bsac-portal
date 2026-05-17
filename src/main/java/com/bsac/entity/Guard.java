package com.bsac.entity;

import javax.persistence.*;

@Entity
public class Guard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String loginCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getLoginCode() { return loginCode; }
    public void setLoginCode(String loginCode) { this.loginCode = loginCode; }
}
