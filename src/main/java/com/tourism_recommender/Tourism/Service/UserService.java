package com.tourism_recommender.Tourism.Service;

import com.tourism_recommender.Tourism.Model.User;
import com.tourism_recommender.Tourism.Model.UserServiceI;
import com.tourism_recommender.Tourism.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserServiceI {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> otpStorage = new HashMap<>();



    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean saveUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception for debugging
            return false;
        }
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean sendOtpToEmail(String email) {
        if (!checkEmailExists(email)) {
            return false;
        }

        String otp = generateOtp();
        otpStorage.put(email, otp);
        return sendEmail(email, otp);
    }

    private boolean sendEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUser(String email, String password) {
        // Fetch user from database by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();
        // Check if the password matches
        return passwordEncoder.matches(password, user.getPassword());
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }

    public boolean changePassword(String email, String newPassword) {
        System.out.println("Changing password for email: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            System.out.println("User not found");
            return false;
        }

        User user = userOptional.get();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        System.out.println("Password changed successfully");
        return true;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

