import React, { useState, useEffect } from 'react';
import { Form, Button, ListGroup, Spinner, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { postAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { FaTrash } from 'react-icons/fa';

function CommentSection({ postId, onCommentAdded }) {
    const { user: currentUser } = useAuth();
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    useEffect(() => {
        console.log('CommentSection mounted with postId:', postId);
        loadComments();
    }, [postId]);

    const loadComments = async (pageNum = 0) => {
        try {
            setLoading(true);
            console.log('Loading comments for postId:', postId);
            const response = await postAPI.getComments(postId, pageNum, 20);
            console.log('Comments response:', response);
            console.log('Comments data:', response.data);

            const newComments = response.data.content || [];
            console.log('New comments:', newComments);

            if (pageNum === 0) {
                setComments(newComments);
            } else {
                setComments((prev) => [...prev, ...newComments]);
            }

            setHasMore(response.data.last === false);
            setPage(pageNum);
            console.log('Comments state updated with', newComments.length, 'comments');
        } catch (error) {
            console.error('Error loading comments:', error);
            setComments([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;

        setSubmitting(true);
        try {
            const response = await postAPI.addComment(postId, { content: newComment });
            setComments((prev) => [response.data, ...prev]);
            setNewComment('');
            onCommentAdded && onCommentAdded();
        } catch (error) {
            console.error('Error adding comment:', error);
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeleteComment = async (commentId) => {
        if (window.confirm('Delete this comment?')) {
            try {
                await postAPI.deleteComment(commentId);
                setComments((prev) => prev.filter((comment) => comment.id !== commentId));
            } catch (error) {
                console.error('Error deleting comment:', error);
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
        <div className="mt-3 border-top pt-3 bg-light p-3 rounded">
            {/* Comment Input Form */}
            <Form onSubmit={handleSubmit} className="mb-4">
                <Form.Group>
                    <Form.Control
                        as="textarea"
                        rows={2}
                        placeholder="Write a comment..."
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        maxLength={280}
                        disabled={submitting}
                    />
                    <div className="d-flex justify-content-between align-items-center mt-2">
                        <Form.Text className="text-muted">{newComment.length}/280</Form.Text>
                        <Button
                            variant="primary"
                            size="sm"
                            type="submit"
                            disabled={submitting || !newComment.trim()}
                        >
                            {submitting ? 'Posting...' : 'Comment'}
                        </Button>
                    </div>
                </Form.Group>
            </Form>

            {/* Loading State */}
            {loading ? (
                <div className="text-center py-4">
                    <Spinner animation="border" variant="primary" size="sm" />
                    <p className="text-muted mt-2">Loading comments...</p>
                </div>
            ) : (
                <>
                    {/* Comments Count */}
                    {comments.length > 0 && (
                        <div className="mb-3">
                            <h6 className="text-muted">
                                💬 {comments.length} {comments.length === 1 ? 'Comment' : 'Comments'}
                            </h6>
                        </div>
                    )}

                    {/* No Comments Message */}
                    {comments.length === 0 && (
                        <Alert variant="info" className="mb-0">
                            No comments yet. Be the first to comment!
                        </Alert>
                    )}

                    {/* Comments List */}
                    {comments.length > 0 && (
                        <ListGroup variant="flush" className="bg-transparent">
                            {comments.map((comment) => (
                                <ListGroup.Item
                                    key={comment.id}
                                    className="px-3 py-2 bg-white mb-2 rounded border"
                                >
                                    <div className="d-flex justify-content-between align-items-start">
                                        <div className="flex-grow-1">
                                            <div className="d-flex align-items-center gap-2">
                                                <Link
                                                    to={`/profile/${comment.username}`}
                                                    className="text-decoration-none text-dark"
                                                >
                                                    <strong>{comment.displayName || comment.username}</strong>
                                                </Link>
                                                <span className="text-muted small">@{comment.username}</span>
                                                <span className="text-muted small">· {formatDate(comment.createdAt)}</span>
                                            </div>
                                            <p className="mb-0 mt-2">{comment.content}</p>
                                        </div>
                                        {currentUser?.username === comment.username && (
                                            <Button
                                                variant="link"
                                                size="sm"
                                                className="text-danger p-0 ms-2"
                                                onClick={() => handleDeleteComment(comment.id)}
                                                title="Delete comment"
                                            >
                                                <FaTrash />
                                            </Button>
                                        )}
                                    </div>
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                    )}

                    {/* Load More Comments */}
                    {hasMore && comments.length > 0 && (
                        <div className="text-center mt-3">
                            <Button
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => loadComments(page + 1)}
                            >
                                Load More Comments
                            </Button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

export default CommentSection;
