package com.API.BlogV2.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String displayName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 500)
    private String bio;

    @Column(length = 100)
    private String occupation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Column(name = "profile_pic_url")
    private String profilePicUrl;       // Full ImageKit URL stored here


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Post> posts;


    // 🔹 Auto timestamp handling
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }




    public String generateDisplayName(String name) {;
        String cleaned = name
                .toLowerCase()
                .replaceAll("[^a-z0-9]", ""); // remove special chars

        // take first 5 characters safely
        if (cleaned.length() > 5) {
            cleaned = cleaned.substring(0, 5);
        }
        return cleaned;
    }


    public void setDisplayName(String displayName) {
        this.displayName = generateDisplayName(this.name);
    }


}