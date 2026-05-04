package com.API.BlogV2.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @SequenceGenerator(
            name = "post_sequence",
            sequenceName = "post_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    private int commentCount = 0;

    // Relationship with User (author of post)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"posts", "password", "hibernateLazyInitializer"})
    private User user;

    // Comments
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    // Categories (Enum)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_categories", joinColumns = @JoinColumn(name = "post_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<CategoryType> categories = new HashSet<>();

    @Column(name = "cover_image_url")
    private String coverImageUrl;       // Full ImageKit URL stored here

    // Getter & Setter
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    // Constructors
    public Post() {}

    public Post(String title, String content, User user, String coverImageUrl) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.coverImageUrl = coverImageUrl;
    }



    //  Lifecycle Hooks
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // Helper methods

    public void addCategory(CategoryType category) {
        this.categories.add(category);
    }

    public void removeCategory(CategoryType category) {
        this.categories.remove(category);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
        this.commentCount = comments.size(); // keep in sync
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
        this.commentCount = comments.size();
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Set<CategoryType> getCategories() {
        return categories;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}