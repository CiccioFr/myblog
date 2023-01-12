package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    // @Autowired - iniettiamo il repository
    @Autowired
    PostRepository postRepository;

    public void save(Post p) {
        postRepository.save(p);
    }

    public boolean existsByTitle(String title) {
        return postRepository.existsByTitle(title);
    }

    public Optional<Post> findById(long id){
        return postRepository.findById(id);
    }
}
