package com.API.BlogV2.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.API.BlogV2.Exception.ResourceNotFoundException;
import com.API.BlogV2.Exception.BlogAPIException;
import com.API.BlogV2.DTO.CommentDTO;
import com.API.BlogV2.DTO.PostMapper;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;
    private final PostMapper postMapper;




    @Transactional(readOnly = true)
    public List<CommentDTO> getAllComments(Long post_id) {
        if (!postRepository.existsById(post_id)) {
            throw new ResourceNotFoundException("Post", "id", post_id);
        }
        // Match the new repository method name
        List<Comment> comments =  commentRepository.findByPost_Id(post_id);

        return comments.stream()
                .map(postMapper::commentToCommentDTO)
                .collect(Collectors.toList());

    }


    @Transactional
    public  void addComment(Long postId,Long userId, String Content){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
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
            throw new ResourceNotFoundException("Post", "id", postId);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        //Verify Comment belongs to the Post
        if (!comment.getPost().getId().equals(postId)) {
            throw new BlogAPIException(org.springframework.http.HttpStatus.BAD_REQUEST, "Comment-Post mismatch");
        }

        // Only the creator can edit
        if (!comment.getUser().getId().equals(userId)) {
            throw new BlogAPIException(org.springframework.http.HttpStatus.FORBIDDEN, "You can only edit your own comments");
        }

        comment.setContent(newContent);
    }


    @Transactional
    public void deleteComment(Long postId, Long userId,Long commentId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        //checking whether this comment is belong to this post or not
        if (!comment.getPost().getId().equals(postId)) {
            throw new BlogAPIException(org.springframework.http.HttpStatus.BAD_REQUEST, "Comment does not belong to this post");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        boolean isOwner = comment.getUser().getId().equals(userId);
        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new BlogAPIException(org.springframework.http.HttpStatus.FORBIDDEN, "You are not authorized to delete this comment");
        }

        // 5. Perform deletion
        commentRepository.delete(comment);


    }
}
