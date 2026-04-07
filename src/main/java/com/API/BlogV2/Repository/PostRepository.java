package com.API.BlogV2.Repository;

import com.API.BlogV2.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);
}
