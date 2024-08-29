package com.tourism_recommender.Tourism.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String phone;
    private String address;

    @Lob
    private byte[] profilePic; // Optional: Store profile picture as binary data
}
