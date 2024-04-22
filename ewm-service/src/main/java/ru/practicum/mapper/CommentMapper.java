package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.CommentDto;
import ru.practicum.model.Comment;


@UtilityClass
public class CommentMapper {

    public static Comment toComment(CommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.mapToUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .state(comment.getState().toString())
                .createdOn(comment.getCreatedOn().format(FormatConstants.FORMATTER))
                .updatedOn(comment.getUpdatedOn() != null ? comment.getUpdatedOn().format(FormatConstants.FORMATTER) : null)
                .publishedOn(comment.getPublishedOn() != null ? comment.getPublishedOn().format(FormatConstants.FORMATTER) : null)
                .build();
    }
}