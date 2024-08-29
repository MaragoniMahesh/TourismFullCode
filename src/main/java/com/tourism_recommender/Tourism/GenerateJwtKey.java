package com.tourism_recommender.Tourism;

import io.jsonwebtoken.security.Keys;

public class GenerateJwtKey {
    public static void main(String[] args) {
        // Generate a secure key
        var key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        // Print the key in Base64 format
        System.out.println("Generated Key: " + key.getEncoded());
    }
}

