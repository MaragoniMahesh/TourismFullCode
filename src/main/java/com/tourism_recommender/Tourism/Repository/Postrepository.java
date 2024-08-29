package com.tourism_recommender.Tourism.Repository;

import com.tourism_recommender.Tourism.Model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Postrepository extends JpaRepository<Posts , Long> {


    @Query("SELECT p FROM Posts p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.location) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Posts> searchPostsByQuery(@Param("query") String query);
}
