package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.groups.Create;
import ru.practicum.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/user/{userId}/events/{eventId}/comment/")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long eventId,
                             @RequestBody @Validated({Create.class}) CommentDto newCommentDto) {
        log.info("POST comment = {}", newCommentDto);
        return commentService.create(userId, eventId, newCommentDto);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable @Positive Long eventId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET all comments for event with id = {}, from = {}, size = {}", eventId, from, size);
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/comment/{commentId}")
    public CommentDto getById(@PathVariable @Positive Long commentId) {
        log.info("GET comment with id = {}", commentId);
        return commentService.getById(commentId);
    }

    @PatchMapping("/user/{userId}/comment/{commentId}")
    public CommentDto update(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long commentId,
                             @RequestBody @Validated({Create.class}) CommentDto newCommentDto) {
        log.info("UPDATE comment = {} with id = {}", newCommentDto, commentId);
        return commentService.update(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId,
                       @PathVariable @Positive Long commentId) {
        log.info("DELETE comment with id = {}", commentId);
        commentService.delete(userId, commentId);
    }

    @PatchMapping("/admin/comment/{commentId}")
    public CommentDto updateCommentStatusByAdmin(@PathVariable @Positive Long commentId, @RequestParam boolean isConfirm) {
        log.info("UPDATE comment with id = {}, isConfirm = {}", commentId, isConfirm);
        return commentService.updateCommentStatusByAdmin(commentId, isConfirm);
    }
}