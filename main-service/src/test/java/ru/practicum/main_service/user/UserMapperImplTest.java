package ru.practicum.main_service.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main_service.user.domain.model.User;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.dto.UserShortDto;
import ru.practicum.main_service.user.mapper.UserMapperImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class UserMapperImplTest {
    @InjectMocks
    private UserMapperImpl userMapper;

    private final NewUserRequest newUserRequest = NewUserRequest.builder()
            .name("user 1")
            .email("test1@yandex.ru")
            .build();
    private final User user = User.builder()
            .id(1L)
            .name(newUserRequest.getName())
            .email(newUserRequest.getEmail())
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    private final UserShortDto userShortDto = UserShortDto.builder()
            .id(user.getId())
            .name(user.getName())
            .build();

    @Nested
    class ToUser {
        @Test
        public void toUser() {
            User result = userMapper.toUser(newUserRequest);

            assertNull(result.getId());
            assertEquals(newUserRequest.getName(), result.getName());
            assertEquals(newUserRequest.getEmail(), result.getEmail());
        }

        @Test
        public void toUserWhenNull() {
            User result = userMapper.toUser(null);

            assertNull(result);
        }
    }

    @Nested
    class ToUserDto {
        @Test
        public void toUserDto() {
            UserDto result = userMapper.toUserDto(user);

            assertEquals(userDto.getId(), result.getId());
            assertEquals(userDto.getName(), result.getName());
            assertEquals(userDto.getEmail(), result.getEmail());
        }

        @Test
        public void toUserDtoWhenNull() {
            UserDto result = userMapper.toUserDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToUserShortDto {
        @Test
        public void toUserShortDto() {
            UserShortDto result = userMapper.toUserShortDto(user);

            assertEquals(userShortDto.getId(), result.getId());
            assertEquals(userShortDto.getName(), result.getName());
        }

        @Test
        public void toUserShortDtoWhenNull() {
            UserShortDto result = userMapper.toUserShortDto(null);

            assertNull(result);
        }
    }
}
