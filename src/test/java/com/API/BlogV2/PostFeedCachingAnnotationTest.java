package com.API.BlogV2;

import com.API.BlogV2.Service.CommentServiceImpl;
import com.API.BlogV2.Service.PostService;
import com.API.BlogV2.Service.PostServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostFeedCachingAnnotationTest {

    @Test
    @DisplayName("Verify getAllPostsSummary is annotated with @Cacheable for allPostsSummary")
    void testGetAllPostsSummaryHasCacheableAnnotation() throws NoSuchMethodException {
        Method method = PostServiceImpl.class.getMethod("getAllPostsSummary", Pageable.class);
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        assertNotNull(cacheable, "getAllPostsSummary should have @Cacheable annotation");
        assertTrue(Arrays.asList(cacheable.value()).contains("allPostsSummary"),
                "Cache name should be 'allPostsSummary'");
        assertEquals("#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort",
                cacheable.key(), "Cache key should incorporate page number, size, and sort");
    }

    @Test
    @DisplayName("Verify legacy getAllPosts is marked @Deprecated in PostService and PostServiceImpl")
    void testLegacyGetAllPostsIsDeprecated() throws NoSuchMethodException {
        Method interfaceMethod = PostService.class.getMethod("getAllPosts", int.class, int.class);
        assertTrue(interfaceMethod.isAnnotationPresent(Deprecated.class),
                "PostService.getAllPosts should be marked @Deprecated");

        Method implMethod = PostServiceImpl.class.getMethod("getAllPosts", int.class, int.class);
        assertTrue(implMethod.isAnnotationPresent(Deprecated.class),
                "PostServiceImpl.getAllPosts should be marked @Deprecated");
    }

    @Test
    @DisplayName("Verify mutation methods in PostServiceImpl evict 'allPostsSummary'")
    void testPostServiceImplEvictsAllPostsSummary() {
        List<String> mutationMethodNames = List.of(
                "addNewPost",
                "updatePost",
                "deletePost",
                "likePost",
                "updateCoverImage"
        );

        for (Method method : PostServiceImpl.class.getDeclaredMethods()) {
            if (mutationMethodNames.contains(method.getName())) {
                Caching caching = method.getAnnotation(Caching.class);
                assertNotNull(caching, "Method " + method.getName() + " should have @Caching annotation");

                boolean evictsAllPostsSummary = Arrays.stream(caching.evict())
                        .anyMatch(evict -> Arrays.asList(evict.value()).contains("allPostsSummary") && evict.allEntries());

                assertTrue(evictsAllPostsSummary,
                        "Method " + method.getName() + " must evict 'allPostsSummary' with allEntries = true");
            }
        }
    }

    @Test
    @DisplayName("Verify comment mutations in CommentServiceImpl evict 'allPostsSummary'")
    void testCommentServiceImplEvictsAllPostsSummary() {
        List<String> commentMutationMethods = List.of(
                "addComment",
                "deleteComment"
        );

        for (Method method : CommentServiceImpl.class.getDeclaredMethods()) {
            if (commentMutationMethods.contains(method.getName())) {
                Caching caching = method.getAnnotation(Caching.class);
                assertNotNull(caching, "Method " + method.getName() + " should have @Caching annotation");

                boolean evictsAllPostsSummary = Arrays.stream(caching.evict())
                        .anyMatch(evict -> Arrays.asList(evict.value()).contains("allPostsSummary") && evict.allEntries());

                assertTrue(evictsAllPostsSummary,
                        "Method " + method.getName() + " must evict 'allPostsSummary' with allEntries = true");
            }
        }
    }
}
