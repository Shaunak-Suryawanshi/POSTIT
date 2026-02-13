package com.postit.service;

import com.postit.dto.CommentResponse;
import com.postit.dto.CreateCommentRequest;
import com.postit.dto.CreatePostRequest;
import com.postit.dto.PostResponse;
import com.postit.event.PostCommentedEvent;
import com.postit.event.PostLikedEvent;
import com.postit.exception.ResourceNotFoundException;
import com.postit.kafka.NotificationProducer;
import com.postit.model.Comment;
import com.postit.model.Post;
import com.postit.model.User;
import com.postit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final NotificationProducer notificationProducer;

    @Transactional
    public PostResponse createPost(CreatePostRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .userId(userId)
                .content(request.getContent())
                .build();

        post = postRepository.save(post);
        log.info("Post created by user: {}", user.getUsername());

        return mapToPostResponse(post, user, userId);
    }

    public Page<PostResponse> getFeed(String currentUserId, Pageable pageable) {
        // Get a list of users that current user follows
        List<String> followingIds = followRepository.findByFollowerId(currentUserId)
                .stream()
                .map(follow -> follow.getFollowingId())
                .collect(Collectors.toList());

        // Add current user's ID to see their own posts
        followingIds.add(currentUserId);

        // Get posts from followed users
        Page<Post> posts;
        if (followingIds.isEmpty()) {
            posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        } else {
            posts = postRepository.findByUserIdInAndIsDeletedFalseOrderByCreatedAtDesc(followingIds, pageable);
        }

        return posts.map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            return mapToPostResponse(post, user, currentUserId);
        });
    }

    public Page<PostResponse> getUserPosts(String userId, String currentUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);
        User user = userRepository.findById(userId).orElse(null);

        return posts.map(post -> mapToPostResponse(post, user, currentUserId));
    }

    public PostResponse getPost(String postId, String currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.isDeleted()) {
            throw new ResourceNotFoundException("Post not found");
        }

        User user = userRepository.findById(post.getUserId()).orElse(null);
        return mapToPostResponse(post, user, currentUserId);
    }

    @Transactional
    public void deletePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own posts");
        }

        post.setDeleted(true);
        postRepository.save(post);
        log.info("Post deleted: {}", postId);
    }

    @Transactional
    public void likePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            com.postit.model.Like like = com.postit.model.Like.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();
            likeRepository.save(like);

            // Update like count
            post.setLikeCount((int) likeRepository.countByPostId(postId));
            postRepository.save(post);

            // Publish event to Kafka
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            PostLikedEvent event = PostLikedEvent.builder()
                .likeId(like.getId())
                .postId(postId)
                .postOwnerId(post.getUserId())
                .likeUserId(userId)
                .likerUsername(user.getUsername())
                .timestamp(LocalDateTime.now())
                .build();

            notificationProducer.publishPostLiked(event);

            log.info("Post liked: {} by user: {}", postId, userId);
        }
    }

    @Transactional
    public void unlikePost(String postId, String userId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);

            // Update like count
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            post.setLikeCount((int) likeRepository.countByPostId(postId));
            postRepository.save(post);

            log.info("Post unliked: {} by user: {}", postId, userId);
        }
    }

    @Transactional
    public CommentResponse addComment(String postId, CreateCommentRequest request, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(request.getContent())
                .build();

        comment = commentRepository.save(comment);

        // Update comment count
        post.setCommentCount((int) commentRepository.countByPostIdAndIsDeletedFalse(postId));
        postRepository.save(post);

        // Publish event to Kafka
        PostCommentedEvent event = PostCommentedEvent.builder()
            .commentId(comment.getId())
            .postId(postId)
            .postOwnerId(post.getUserId())
            .commentUserId(userId)
            .commenterUsername(user.getUsername())
            .commentContent(request.getContent())
            .timestamp(LocalDateTime.now())
            .build();

        notificationProducer.publishPostCommented(event);

        log.info("Comment added to post: {} by user: {}", postId, user.getUsername());

        return mapToCommentResponse(comment, user);
    }

    public Page<CommentResponse> getComments(String postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(postId, pageable);

        return comments.map(comment -> {
            User user = userRepository.findById(comment.getUserId()).orElse(null);
            return mapToCommentResponse(comment, user);
        });
    }

    @Transactional
    public void deleteComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);

        // Update comment count
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setCommentCount((int) commentRepository.countByPostIdAndIsDeletedFalse(comment.getPostId()));
        postRepository.save(post);

        log.info("Comment deleted: {}", commentId);
    }

    private PostResponse mapToPostResponse(Post post, User user, String currentUserId) {
        boolean likedByCurrentUser = false;
        if (currentUserId != null) {
            likedByCurrentUser = likeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
        }

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .username(user != null ? user.getUsername() : "Unknown")
                .displayName(user != null ? user.getDisplayName() : "Unknown")
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private CommentResponse mapToCommentResponse(Comment comment, User user) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .username(user != null ? user.getUsername() : "Unknown")
                .displayName(user != null ? user.getDisplayName() : "Unknown")
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
