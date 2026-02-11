package com.minicollaborationboard.domain.comment.repository;

import com.minicollaborationboard.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTicketId(Long ticketId);

    void deleteAllByTicketId(Long ticketId);
}
