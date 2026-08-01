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
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import com.API.BlogV2.DTO.PostSummaryDTO;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT new com.API.BlogV2.DTO.PostSummaryDTO(" +
           "p.id, p.title, p.slug, SUBSTRING(p.content, 1, 150), p.user.displayName, " +
           "p.coverImageUrl, p.createdAt, p.commentCount, p.likeCount) " +
           "FROM Post p WHERE p.status = :status")
    Page<PostSummaryDTO> findProjectedByStatus(@Param("status") PostStatus status, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
           "LEFT JOIN FETCH p.tags " +
           "LEFT JOIN FETCH p.comments c " +
           "WHERE p.slug = :slug AND p.status = 'PUBLISHED'")
    Optional<Post> findBySlugWithTagsAndComments(@Param("slug") String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.slug = :slug")
    void incrementViewCountBySlug(@Param("slug") String slug);

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

