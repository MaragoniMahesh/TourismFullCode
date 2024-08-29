package com.tourism_recommender.Tourism.Service;

import com.tourism_recommender.Tourism.Model.Posts;

import com.tourism_recommender.Tourism.Repository.Postrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

   @Autowired
   private Postrepository postrepository;

    private final String UPLOAD_DIR = "uploads/";

    public Posts savePost(String location, String description, MultipartFile image) throws IOException {
        Posts post = new Posts();
        post.setLocation(location);
        post.setDescription(description);

        if (image != null && !image.isEmpty()) {
            post.setImage(image.getBytes());
            post.setImageType(image.getContentType());
        }

        return postrepository.save(post);
    }


    public List<Posts> getAllPosts() {
        return postrepository.findAll();
    }
    public Optional<Posts> findById(Long id) {
        return postrepository.findById(id);
    }

    public List<Posts> searchPosts(String query) {
        return postrepository.searchPostsByQuery(query);
    }
}
