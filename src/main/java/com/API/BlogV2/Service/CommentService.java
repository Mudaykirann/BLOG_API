package com.API.BlogV2.Service;

import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.CommentRepository;
import com.API.BlogV2.Repository.PostRepository;
import com.API.BlogV2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;


    public List<Comment> getAllComments(Long post_id) {
        if (!postRepository.existsById(post_id)) {
            throw new NoSuchElementException("Post not found");
        }
        // Match the new repository method name
        return commentRepository.findByPost_Id(post_id);
    }


    @Transactional
    public  void addComment(Long postId,Long userId, String Content){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        User user = userRepository.getReferenceById(userId);

        Comment comment = new Comment();
        comment.setContent(Content);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);
    }
}
