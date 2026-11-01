package com.API.BlogV2;

import com.API.BlogV2.Controller.FeedController;
import com.API.BlogV2.Controller.PostController;
import com.API.BlogV2.Controller.UserController;
import com.API.BlogV2.DTO.PostResponseDTO;
import com.API.BlogV2.Exception.UnifiedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseContractAndConfigTest {

    @Test
    @DisplayName("Verify PostController.getPostById returns ResponseEntity<UnifiedResponse<PostResponseDTO>>")
    void testGetPostByIdReturnsUnifiedResponse() throws NoSuchMethodException {
        Method method = PostController.class.getMethod("getPostById", Long.class);
        assertEquals(ResponseEntity.class, method.getReturnType());

        Type genericReturnType = method.getGenericReturnType();
        assertTrue(genericReturnType instanceof ParameterizedType);

        Type responseEntityArg = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        assertTrue(responseEntityArg instanceof ParameterizedType);

        ParameterizedType unifiedResponse = (ParameterizedType) responseEntityArg;
        assertEquals(UnifiedResponse.class, unifiedResponse.getRawType());
        assertEquals(PostResponseDTO.class, unifiedResponse.getActualTypeArguments()[0]);
    }

    @Test
    @DisplayName("Verify PostController.getCoverThumbnail returns ResponseEntity<UnifiedResponse<Map<String, String>>>")
    void testGetCoverThumbnailReturnsUnifiedResponse() throws NoSuchMethodException {
        Method method = PostController.class.getMethod("getCoverThumbnail", Long.class, int.class, int.class);
        assertEquals(ResponseEntity.class, method.getReturnType());

        Type genericReturnType = method.getGenericReturnType();
        assertTrue(genericReturnType instanceof ParameterizedType);

        Type responseEntityArg = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        assertTrue(responseEntityArg instanceof ParameterizedType);

        ParameterizedType unifiedResponse = (ParameterizedType) responseEntityArg;
        assertEquals(UnifiedResponse.class, unifiedResponse.getRawType());
        assertEquals(Map.class, ((ParameterizedType) unifiedResponse.getActualTypeArguments()[0]).getRawType());
    }

    @Test
    @DisplayName("Verify UserController.getProfilePicThumbnail returns ResponseEntity<UnifiedResponse<Map<String, String>>>")
    void testGetProfilePicThumbnailReturnsUnifiedResponse() throws NoSuchMethodException {
        Method method = UserController.class.getMethod("getProfilePicThumbnail", Long.class, int.class, int.class);
        assertEquals(ResponseEntity.class, method.getReturnType());

        Type genericReturnType = method.getGenericReturnType();
        assertTrue(genericReturnType instanceof ParameterizedType);

        Type responseEntityArg = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        assertTrue(responseEntityArg instanceof ParameterizedType);

        ParameterizedType unifiedResponse = (ParameterizedType) responseEntityArg;
        assertEquals(UnifiedResponse.class, unifiedResponse.getRawType());
        assertEquals(Map.class, ((ParameterizedType) unifiedResponse.getActualTypeArguments()[0]).getRawType());
    }

    @Test
    @DisplayName("Verify schema.sql contains PostgreSQL GIN index for full-text search")
    void testSchemaSqlContainsGinIndex() throws Exception {
        ClassPathResource resource = new ClassPathResource("schema.sql");
        assertTrue(resource.exists(), "schema.sql must exist in resources");

        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(content.contains("CREATE INDEX IF NOT EXISTS idx_fts ON posts USING gin"),
                    "schema.sql must contain GIN index on posts table");
            assertTrue(content.contains("to_tsvector('english', title || ' ' || content)"),
                    "schema.sql must index tsvector expression over title and content");
        }
    }
}
