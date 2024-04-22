package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.type.CommentState;
import ru.practicum.dto.type.PublicationState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationConflictException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto create(Long userId, Long eventId, CommentDto newCommentDto) {
        log.info("Create comment from user with ID = {}, for event with ID = {}, newCommentDto = {}", userId, eventId, newCommentDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Event event = eventRepository.findByIdAndPublicationState(eventId, PublicationState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        Comment comment = CommentMapper.toComment(newCommentDto);
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setState(CommentState.PENDING);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        log.info("Get comments for event with ID = {}", eventId);
        Event event = eventRepository.findByIdAndPublicationState(eventId, PublicationState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        List<Comment> comments = commentRepository.findByEvent(event, PageRequest.of(from / size, size));
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getById(Long commentId) {
        log.info("Get comment by ID = {}", commentId);
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND.getValue(), commentId))));
    }

    @Override
    public CommentDto update(Long userId, Long commentId, CommentDto newCommentDto) {
        log.info("Update comment with ID = {} from user with ID = {} , newCommentDto = {}", userId, commentId, newCommentDto);
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND.getValue(), commentId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationConflictException(String.format(COMMENT_USER_CONFLICT.getValue()));
        }
        if (comment.getState() == CommentState.CONFIRMED) {
            throw new ValidationConflictException(String.format(COMMENT_ALREADY_CONFIRMED.getValue()));
        }
        comment.setText(newCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        comment.setState(CommentState.PENDING);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long userId, Long commentId) {
        log.info("Delete comment with ID = {} from user with ID = {}", commentId,userId);
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND.getValue(), commentId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationConflictException(String.format(COMMENT_USER_CONFLICT.getValue()));
        }
        if (comment.getState() == CommentState.CONFIRMED) {
            throw new ValidationConflictException(String.format(COMMENT_ALREADY_CONFIRMED.getValue()));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto updateCommentStatusByAdmin(Long commentId, boolean isConfirm) {
        log.info("Confirm/reject comment with ID = {}. New state: {}", commentId,isConfirm);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND.getValue(), commentId)));
        if (isConfirm) {
            comment.setState(CommentState.CONFIRMED);
        } else {
            comment.setState(CommentState.REJECTED);
        }
        comment.setPublishedOn(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}