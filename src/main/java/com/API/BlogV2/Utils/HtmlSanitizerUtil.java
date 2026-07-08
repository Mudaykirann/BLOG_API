package com.API.BlogV2.Utils;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

/**
 * Utility to sanitize HTML content submitted by authors.
 *
 * This prevents XSS (Cross-Site Scripting) attacks by stripping dangerous
 * tags like <script>, <iframe>, or <object> while keeping safe, rich
 * formatting tags that are standard in Markdown-rendered output.
 *
 * Allowed elements: headings, paragraphs, blockquote, bold/italic/underline,
 * code blocks, links (with safe protocols only), ordered/unordered lists.
 */
@Component
public class HtmlSanitizerUtil {

    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
            // Structural / Text elements
            .allowElements(
                    "p", "br", "hr",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "blockquote", "pre", "code",
                    "b", "strong", "i", "em", "u", "s", "strike",
                    "ul", "ol", "li",
                    "table", "thead", "tbody", "tr", "th", "td",
                    "a", "img", "span", "div"
            )
            // Allow href on <a> but only with safe protocols
            .allowUrlProtocols("https", "http", "mailto")
            .allowAttributes("href").onElements("a")
            .allowAttributes("target", "rel").onElements("a")
            // Allow src on <img> (for embedded images)
            .allowAttributes("src", "alt", "width", "height").onElements("img")
            // Allow standard text formatting attributes
            .allowAttributes("class").globally()
            .toFactory();

    /**
     * Sanitizes the given HTML/Markdown-rendered string.
     * Strips any elements not explicitly allowed above.
     *
     * @param rawHtml the raw HTML string from the client
     * @return a safe, sanitized HTML string
     */
    public String sanitize(String rawHtml) {
        if (rawHtml == null || rawHtml.isBlank()) {
            return rawHtml;
        }
        return POLICY.sanitize(rawHtml);
    }
}
