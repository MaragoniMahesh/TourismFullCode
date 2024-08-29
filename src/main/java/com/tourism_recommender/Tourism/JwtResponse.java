package com.tourism_recommender.Tourism;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {
    @JsonProperty("token")
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
