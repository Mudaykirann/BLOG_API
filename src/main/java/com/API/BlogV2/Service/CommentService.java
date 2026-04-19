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

    @Transactional
    public void updateComment(Long postId, Long userId, Long commentId, String newContent) {

        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("Post not found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        //Verify Comment belongs to the Post
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment-Post mismatch");
        }

        // Only the creator can edit
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only edit your own comments");
        }

        comment.setContent(newContent);
    }


    @Transactional
    public void deleteComment(Long postId, Long userId,Long commentId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        //checking whether this comment is belong to this post or not
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to this post");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        boolean isOwner = comment.getUser().getId().equals(userId);
        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new IllegalStateException("You are not authorized to delete this comment");
        }

        // 5. Perform deletion
        commentRepository.delete(comment);


    }
}
