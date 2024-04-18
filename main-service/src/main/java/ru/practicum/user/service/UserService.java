package ru.practicum.user.service;

import java.util.List;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

public interface UserService {

    UserDto createUser(NewUserDto newUserDto);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(long userId);
}
