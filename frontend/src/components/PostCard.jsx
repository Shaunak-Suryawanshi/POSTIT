import React, { useState } from 'react';
import { Card, Button, Form, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { FaHeart, FaRegHeart, FaComment, FaTrash } from 'react-icons/fa';
import { postAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import CommentSection from './CommentSection';

function PostCard({ post, onDelete, onUpdate }) {
    const { user } = useAuth();
    const [showComments, setShowComments] = useState(false);
    const [liked, setLiked] = useState(post.likedByCurrentUser);
    const [likeCount, setLikeCount] = useState(post.likeCount);

    const handleLike = async () => {
        try {
            if (liked) {
                await postAPI.unlikePost(post.id);
                setLiked(false);
                setLikeCount((prev) => prev - 1);
            } else {
                await postAPI.likePost(post.id);
                setLiked(true);
                setLikeCount((prev) => prev + 1);
            }

            // Update parent component
            onUpdate({
                ...post,
                likedByCurrentUser: !liked,
                likeCount: liked ? likeCount - 1 : likeCount + 1,
            });
        } catch (error) {
            console.error('Error toggling like:', error);
        }
    };

    const handleDelete = async () => {
        if (window.confirm('Are you sure you want to delete this post?')) {
            try {
                await postAPI.deletePost(post.id);
                onDelete(post.id);
            } catch (error) {
                console.error('Error deleting post:', error);
            }
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 1) return 'Just now';
        if (diffMins < 60) return `${diffMins}m ago`;
        if (diffHours < 24) return `${diffHours}h ago`;
        if (diffDays < 7) return `${diffDays}d ago`;
        return date.toLocaleDateString();
    };

    return (
        <Card className="mb-3 shadow-sm border-0">
            <Card.Body>
                {/* Post Header */}
                <div className="d-flex justify-content-between align-items-start mb-3">
                    <div>
                        <Link
                            to={`/profile/${post.username}`}
                            className="text-decoration-none text-dark"
                        >
                            <strong>{post.displayName || post.username}</strong>
                        </Link>
                        <span className="text-muted ms-2">@{post.username}</span>
                        <span className="text-muted ms-2">· {formatDate(post.createdAt)}</span>
                    </div>
                    {user?.username === post.username && (
                        <Button
                            variant="link"
                            size="sm"
                            className="text-danger p-0"
                            onClick={handleDelete}
                            title="Delete post"
                        >
                            <FaTrash />
                        </Button>
                    )}
                </div>

                {/* Post Content */}
                <Card.Text className="mb-3 fs-5">{post.content}</Card.Text>

                {/* Like and Comment Buttons */}
                <div className="d-flex gap-4 mt-3 border-top border-bottom py-2">
                    <Button
                        variant="link"
                        className={`p-0 d-flex align-items-center gap-2 text-decoration-none ${
                            liked ? 'text-danger' : 'text-muted'
                        }`}
                        onClick={handleLike}
                        title={liked ? 'Unlike post' : 'Like post'}
                    >
                        {liked ? <FaHeart /> : <FaRegHeart />}
                        <span>{likeCount}</span>
                    </Button>

                    <Button
                        variant="link"
                        className="p-0 d-flex align-items-center gap-2 text-decoration-none text-muted"
                        onClick={() => setShowComments(!showComments)}
                        title="View and add comments"
                    >
                        <FaComment />
                        <span>{post.commentCount}</span>
                    </Button>
                </div>

                {/* Comments Section */}
                {showComments && (
                    <CommentSection
                        postId={post.id}
                        onCommentAdded={() =>
                            onUpdate({ ...post, commentCount: post.commentCount + 1 })
                        }
                    />
                )}
            </Card.Body>
        </Card>
    );
}

export default PostCard;
