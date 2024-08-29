package com.tourism_recommender.Tourism.Controller;

import com.tourism_recommender.Tourism.Model.Posts;
import com.tourism_recommender.Tourism.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/post")
@Validated
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping()
    public ResponseEntity<?> addPost(
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Posts post = postService.savePost(location, description, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving post: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Posts>> getAllPosts() {
        List<Posts> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<Posts> post = postService.findById(id);
        if (post.isPresent() && post.get().getImage() != null) {
            byte[] image = post.get().getImage();
            String imageType = post.get().getImageType();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(imageType));
            headers.setContentLength(image.length);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Posts>> searchPosts(@RequestParam @NotEmpty String query) {
        List<Posts> posts = postService.searchPosts(query);
        if (posts.isEmpty()) {
            Posts notFoundPost = new Posts();
            notFoundPost.setDescription("Sorry, no posts found matching your query.");
            return ResponseEntity.ok(Collections.singletonList(notFoundPost));
        }
        return ResponseEntity.ok(posts);
    }
}
