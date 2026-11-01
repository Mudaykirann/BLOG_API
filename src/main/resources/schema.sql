-- Full-Text Search GIN Index on posts (title + content)
-- Accelerates PostRepository.searchByKeywordFTS() from sequential table scan to O(log N) inverted index lookup
CREATE INDEX IF NOT EXISTS idx_fts ON posts USING gin(to_tsvector('english', title || ' ' || content));
