package ru.practicum.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.user.model.User;

@UtilityClass
public class UserMapper {
    public User toUser(NewUserDto newUserDto) {
        User user = new User();

        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());

        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();

        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());

        return userShortDto;
    }
}
