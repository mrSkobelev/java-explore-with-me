package ru.practicum.comment.dto;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@UtilityClass
public class CommentMapper {
    public Comment toComment(NewCommentDto commentDto, Event event, User author) {
        Comment comment = new Comment();

        comment.setText(commentDto.getText());
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

    public CommentInfoDto toCommentInfoDto(Comment comment) {
        CommentInfoDto commentInfoDto = new CommentInfoDto();

        commentInfoDto.setId(comment.getId());
        commentInfoDto.setText(comment.getText());
        commentInfoDto.setAuthorEmail(comment.getAuthor().getEmail());
        commentInfoDto.setCreated(comment.getCreated());

        return commentInfoDto;
    }
}
