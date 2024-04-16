package ru.practicum.user.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        log.info("Создать пользователя {}", newUserDto.getEmail());

        User user = UserMapper.toUser(newUserDto);

        try {
            User savedUser = repository.save(user);

            log.info("Зарегистрирован пользователь " + user.getEmail());

            return UserMapper.toUserDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с email: " + user.getEmail()
                + " уже зарегистрирован.");
        }
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Получить пользователей");

        validPagination(from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<User> users;

        if (ids.isEmpty()) {
            users = repository.findAll(pageRequest).getContent();
        } else {
            users = repository.findByIdIn(ids, pageRequest).getContent();
        }

        if (users.isEmpty()) {
            log.info("Список пользователей пуст");

            return Collections.emptyList();
        }

        log.info("Получен список пользователей");

        return users.stream()
            .map(UserMapper::toUserDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Удалить пользователя с id: {}", userId);

        validUser(userId);

        repository.deleteById(userId);

        log.info("Удален пользователь с id: " + userId);
    }

    private User validUser(long userId) {
        return repository.findById(userId).orElseThrow(
            () -> new NotFoundException("Не найден пользователь с id: " + userId));
    }

    private void validPagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не должны быть отрицательными");
        }
    }
}
