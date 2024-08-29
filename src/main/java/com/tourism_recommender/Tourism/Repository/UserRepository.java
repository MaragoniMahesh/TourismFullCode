package com.tourism_recommender.Tourism.Repository;


import com.tourism_recommender.Tourism.Model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Scope("prototype")
public interface UserRepository extends JpaRepository<User, Long> {




    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
