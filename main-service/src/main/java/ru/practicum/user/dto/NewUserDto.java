package ru.practicum.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserDto {
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;

    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    @NotNull
    private String email;
}
