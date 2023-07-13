package ru.practicum.main_service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.domain.model.User;
import ru.practicum.main_service.user.domain.repository.UserRepository;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        log.info("Добавление пользователя {}", newUserRequest);

        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        log.info("Получение пользователей с id {} и пагинацией {}", ids, pageable);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Удаление пользователя с id {}", id);

        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));

        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        log.info("Получение пользователя с id {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));
    }
}
