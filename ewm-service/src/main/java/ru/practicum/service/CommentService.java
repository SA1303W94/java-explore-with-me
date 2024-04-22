package ru.practicum.service;

import ru.practicum.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto create(Long userId, Long eventId, CommentDto newCommentDto);

    List<CommentDto> getEventComments(Long eventId, int from, int size);

    CommentDto getById(Long commentId);

    CommentDto update(Long userId, Long commentId, CommentDto newCommentDto);

    void delete(Long userId, Long commentId);

    CommentDto updateCommentStatusByAdmin(Long commentId, boolean isConfirm);
}