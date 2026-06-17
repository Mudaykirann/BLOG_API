package com.API.BlogV2.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "refreshToken")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date expiryDate;



}
