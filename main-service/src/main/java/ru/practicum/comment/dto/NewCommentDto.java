package ru.practicum.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentDto {
    @Size(min = 3, max = 2000)
    @NotBlank
    private String text;
}
