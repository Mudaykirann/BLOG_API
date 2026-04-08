package com.API.BlogV2.Entity;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {


    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")

    private Long id;
    private String name;
    private String email;
    private String password;
    private String occupation;



    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    private List<Post> posts;

    public User(){}

    public User(String name, String email,String password,String occupation) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.occupation = occupation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
