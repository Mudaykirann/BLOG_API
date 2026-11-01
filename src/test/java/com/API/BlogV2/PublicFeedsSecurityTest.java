package com.API.BlogV2;

import com.API.BlogV2.DTO.PageResponseDTO;
import com.API.BlogV2.DTO.PostSummaryDTO;
import com.API.BlogV2.Service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PublicFeedsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("Verify /feed.xml is publicly accessible without authentication")
    void testRssFeedPubliclyAccessible() throws Exception {
        when(postService.getAllPostsSummary(any()))
                .thenReturn(new PageResponseDTO<>(Collections.emptyList(), 0, 20, 0, 0, true));

        mockMvc.perform(get("/feed.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    @DisplayName("Verify /sitemap.xml is publicly accessible without authentication")
    void testSitemapPubliclyAccessible() throws Exception {
        when(postService.getAllPostsSummary(any()))
                .thenReturn(new PageResponseDTO<>(Collections.emptyList(), 0, 1000, 0, 0, true));

        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }
}
