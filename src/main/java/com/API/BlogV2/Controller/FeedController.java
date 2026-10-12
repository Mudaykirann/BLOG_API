package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.PageResponseDTO;
import com.API.BlogV2.DTO.PostSummaryDTO;
import com.API.BlogV2.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@RestController
public class FeedController {

    private final PostService postService;
    private final String baseUrl = "http://localhost:5173"; // Default base URL for frontend

    @Autowired
    public FeedController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(value = "/feed.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getRssFeed() {
        PageResponseDTO<PostSummaryDTO> response = postService.getAllPostsSummary(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        List<PostSummaryDTO> posts = response.getContent();
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\">\n");
        xml.append("  <channel>\n");
        xml.append("    <title>BlogV2 RSS Feed</title>\n");
        xml.append("    <link>").append(baseUrl).append("</link>\n");
        xml.append("    <description>Latest blog posts</description>\n");
        
        for (PostSummaryDTO post : posts) {
            xml.append("    <item>\n");
            xml.append("      <title><![CDATA[").append(post.getTitle()).append("]]></title>\n");
            xml.append("      <link>").append(baseUrl).append("/post/").append(post.getSlug()).append("</link>\n");
            xml.append("      <description><![CDATA[").append(post.getExcerpt()).append("]]></description>\n");
            if (post.getPublishedAt() != null) {
                xml.append("      <pubDate>").append(sdf.format(post.getPublishedAt())).append("</pubDate>\n");
            }
            xml.append("    </item>\n");
        }
        
        xml.append("  </channel>\n");
        xml.append("</rss>");
        
        return ResponseEntity.ok(xml.toString());
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {
        PageResponseDTO<PostSummaryDTO> response = postService.getAllPostsSummary(
                PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        List<PostSummaryDTO> posts = response.getContent();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        
        // Add root
        xml.append("  <url>\n");
        xml.append("    <loc>").append(baseUrl).append("</loc>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("    <priority>1.0</priority>\n");
        xml.append("  </url>\n");
        
        for (PostSummaryDTO post : posts) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(baseUrl).append("/post/").append(post.getSlug()).append("</loc>\n");
            if (post.getPublishedAt() != null) {
                xml.append("    <lastmod>").append(sdf.format(post.getPublishedAt())).append("</lastmod>\n");
            }
            xml.append("    <changefreq>weekly</changefreq>\n");
            xml.append("    <priority>0.8</priority>\n");
            xml.append("  </url>\n");
        }
        
        xml.append("</urlset>");
        
        return ResponseEntity.ok(xml.toString());
    }
}
