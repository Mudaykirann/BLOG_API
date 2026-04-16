package com.API.BlogV2.Repository;

import com.API.BlogV2.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // The underscore tells JPA: Look at the 'post' field, then find its 'id'
    List<Comment> findByPost_Id(Long postId);
}
