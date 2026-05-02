package com.API.BlogV2.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {



    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3 , max=100,message = "name should between 3 and 100 character")
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Size(min = 5, max=100,message = "occupation should between 5 and 100 character")
    private String occupation;


    private String displayName;
    private String bio;


    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
