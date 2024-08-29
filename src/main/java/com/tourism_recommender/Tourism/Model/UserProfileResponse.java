package com.tourism_recommender.Tourism.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    @Id
    private Long id;

    private String username;
    private String email;
    private String phone;
    private String address;
    private Gender gender;
    private String dateOfBirth;
    private String profilePic; // Base64 encoded


}
