package com.API.BlogV2.Entity;

/**
 * Represents the publishing state of a blog post.
 *
 * - DRAFT     : Saved privately by the author; not visible on the public feed.
 * - PUBLISHED : Visible to everyone on the public feed.
 * - ARCHIVED  : Removed from the public feed but not deleted; retained for history.
 */
public enum PostStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
