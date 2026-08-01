package com.API.BlogV2.Utils;

import com.API.BlogV2.Repository.PostRepository;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
public class SlugGenerator {

    private final PostRepository postRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public SlugGenerator(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public String generateUniqueSlug(String title) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int count = 1;

        while (postRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count;
            count++;
        }
        return slug;
    }

    private String toSlug(String input) {
        if (input == null) return "";
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase().replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
    }
}
