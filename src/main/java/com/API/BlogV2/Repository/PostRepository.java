package com.API.BlogV2.Repository;

import com.API.BlogV2.Entity.CategoryType;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Public feed: only PUBLISHED posts (with comments eagerly loaded)
    @EntityGraph(attributePaths = {"comments"})
    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    // Author's own posts: all statuses (so they can see their drafts too)
    Page<Post> findByUserId(Long userId, Pageable pageable);

    // Search by title — only PUBLISHED posts visible publicly
    List<Post> findByTitleContainingIgnoreCaseAndStatus(String title, PostStatus status);

    // Filter by category — only PUBLISHED
    @Query("SELECT p FROM Post p JOIN p.categories c WHERE c = :category AND p.status = :status")
    List<Post> findByCategoryAndStatus(
            @Param("category") CategoryType category,
            @Param("status") PostStatus status
    );

    // Filter by tag — only PUBLISHED
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t = :tag AND p.status = :status")
    List<Post> findByTagAndStatus(
            @Param("tag") String tag,
            @Param("status") PostStatus status
    );
}

