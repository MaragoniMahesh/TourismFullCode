
package com.tourism_recommender.Tourism.Controller;

import com.tourism_recommender.Tourism.ErrorResponse;
import com.tourism_recommender.Tourism.JwtResponse;
import com.tourism_recommender.Tourism.JwtTokenProvider;
import com.tourism_recommender.Tourism.Model.*;
import com.tourism_recommender.Tourism.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {




    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final UserService userService;


    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(JwtTokenProvider jwtTokenProvider, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("gender") String gender,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("profilePic") MultipartFile profilePic) {

        if (profilePic.getSize() > 10485760) { // Limit to 10 MB
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds limit of 10 MB");
        }

        try {
            LocalDate dob = LocalDate.parse(dateOfBirth); // Convert string to LocalDate
            Gender genderEnum = Gender.valueOf(gender.toUpperCase()); // Convert string to Gender enum

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);

            // Encrypt the password before setting it
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);

            user.setPhone(phone);
            user.setAddress(address);
            user.setGender(genderEnum);
            user.setDateOfBirth(dob);
            user.setProfilePic(profilePic.getBytes()); // Convert MultipartFile to byte array

            boolean isSaved = userService.saveUser(user);
            if (isSaved) {
                return ResponseEntity.ok("Registration successful");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid gender value");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.getUserByEmail(loginRequest.getUseremail());

        // Log failed login attempts for better monitoring
        if (user.isEmpty()) {
            // Return a generic error message to prevent revealing whether the email exists
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid email or password"));
        }

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword());
        if (!passwordMatches) {
            // Return the same generic error message to prevent revealing if the password was incorrect
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid email or password"));
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateJwtToken(user.get());

        // Return the token inside a consistent response structure
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailRequest emailRequest) {
        boolean exists = userService.checkEmailExists(emailRequest.getEmail());
        if (exists) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody EmailRequest emailRequest) {
        boolean success = userService.sendOtpToEmail(emailRequest.getEmail());
        if (success) {
            return ResponseEntity.ok().body("OTP sent successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtp verifyOtp) {
        String email = verifyOtp.getEmail();
        String otp = verifyOtp.getOtp();

        // Validate email and OTP
        boolean success = userService.verifyOtp(email, otp);

        if (success) {
            return ResponseEntity.ok("Otp verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Otp is not correct");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody NewPassword newPassword) {
        if (newPassword.getUseremail() == null || newPassword.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Email or password cannot be null");
        }

        boolean success = userService.changePassword(newPassword.getUseremail(), newPassword.getNewPassword());
        if (success) {
            return ResponseEntity.ok().body("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change password");
        }
    }



    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Fetch the user by email
            Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Encode the profile picture to Base64
                if (user.getProfilePic() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(user.getProfilePic());
                    user.setProfilePicBase64(base64Image);
                }

                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the exception and return an internal server error response
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}








