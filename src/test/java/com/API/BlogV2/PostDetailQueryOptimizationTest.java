package com.API.BlogV2;

import com.API.BlogV2.Repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class PostDetailQueryOptimizationTest {

    @Test
    @DisplayName("Verify findBySlugWithTagsAndComments query fetch-joins comment.user and post.user to eliminate N+1 queries")
    void testFindBySlugWithTagsAndCommentsEagerlyFetchesUsers() throws NoSuchMethodException {
        Method method = PostRepository.class.getMethod("findBySlugWithTagsAndComments", String.class);
        Query query = method.getAnnotation(Query.class);

        assertNotNull(query, "findBySlugWithTagsAndComments should have @Query annotation");
        String queryString = query.value();

        assertTrue(queryString.contains("LEFT JOIN FETCH p.user"),
                "Query should fetch join post author (p.user) to avoid lazy loading of post author");
        assertTrue(queryString.contains("LEFT JOIN FETCH p.comments c"),
                "Query should fetch join comments (p.comments c)");
        assertTrue(queryString.contains("LEFT JOIN FETCH c.user"),
                "Query should fetch join comment authors (c.user) to eliminate N+1 comment user queries");
    }
}
