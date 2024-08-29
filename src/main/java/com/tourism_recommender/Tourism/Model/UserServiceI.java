package com.tourism_recommender.Tourism.Model;

public interface UserServiceI {


    User findByUsername(String username);
    void updateUser(User user);
}
