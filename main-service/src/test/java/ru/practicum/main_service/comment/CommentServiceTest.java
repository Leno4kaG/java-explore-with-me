package ru.practicum.main_service.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.category.domain.model.Category;
import ru.practicum.main_service.comment.domain.model.Comment;
import ru.practicum.main_service.comment.domain.repository.CommentRepository;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.mapper.CommentMapperImpl;
import ru.practicum.main_service.comment.service.CommentService;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.domain.model.Location;
import ru.practicum.main_service.event.domain.repository.EventRepository;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.exception.ForbiddenException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.domain.model.User;
import ru.practicum.main_service.user.domain.repository.UserRepository;
import ru.practicum.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private final Pageable pageable = PageRequest.of(0 / 10, 10);
    private final User user1 = User.builder()
            .id(1L)
            .name("test user 1")
            .email("test1@yandex.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("test user 2")
            .email("test2@yandex.ru")
            .build();
    private final UserShortDto userShortDto1 = UserShortDto.builder()
            .id(user1.getId())
            .name(user1.getName())
            .build();
    private final Category category = Category.builder()
            .id(1L)
            .name("test category")
            .build();
    private final Location location = Location.builder()
            .id(1L)
            .lat(12F)
            .lon(12F)
            .build();
    private final Event event1 = Event.builder()
            .id(1L)
            .annotation("test annotation 1")
            .description("test description 1")
            .title("test title 1")
            .createdOn(LocalDateTime.now().minusDays(7))
            .publishedOn(LocalDateTime.now().minusDays(6))
            .paid(false)
            .state(EventState.PUBLISHED)
            .location(location)
            .category(category)
            .initiator(user1)
            .participantLimit(0)
            .eventDate(LocalDateTime.now().plusHours(3))
            .requestModeration(false)
            .build();
    private final Event event2 = Event.builder()
            .id(2L)
            .annotation("test annotation 2")
            .description("test description 2")
            .title("test title 2")
            .createdOn(LocalDateTime.now().minusDays(7))
            .publishedOn(LocalDateTime.now().minusDays(6))
            .paid(false)
            .state(EventState.PUBLISHED)
            .location(location)
            .category(category)
            .initiator(user1)
            .participantLimit(0)
            .eventDate(LocalDateTime.now().plusHours(3))
            .requestModeration(false)
            .build();
    private final Event event3 = Event.builder()
            .id(3L)
            .annotation("test annotation 3")
            .description("test description 3")
            .title("test title 3")
            .createdOn(LocalDateTime.now().minusDays(3))
            .publishedOn(LocalDateTime.now().minusDays(2))
            .paid(false)
            .state(EventState.PENDING)
            .location(location)
            .category(category)
            .initiator(user1)
            .participantLimit(0)
            .eventDate(LocalDateTime.now().plusHours(5))
            .requestModeration(false)
            .build();
    private final NewCommentDto newCommentDto = NewCommentDto.builder()
            .text("test comment 1")
            .build();
    private final NewCommentDto newCommentDtoToUpdate = NewCommentDto.builder()
            .text("updated test comment 3")
            .build();
    private final Comment comment1 = Comment.builder()
            .id(1L)
            .text(newCommentDto.getText())
            .author(user1)
            .event(event1)
            .createdOn(LocalDateTime.now().minusHours(8))
            .editedOn(null)
            .build();
    private final Comment comment2 = Comment.builder()
            .id(2L)
            .text("test comment 2")
            .author(user1)
            .event(event1)
            .createdOn(LocalDateTime.now().minusHours(7))
            .editedOn(LocalDateTime.now().minusHours(5))
            .build();
    private final Comment comment3 = Comment.builder()
            .id(3L)
            .text("test comment 3")
            .author(user1)
            .event(event2)
            .createdOn(LocalDateTime.now().minusHours(8))
            .editedOn(null)
            .build();
    private final CommentDto commentDto1 = CommentDto.builder()
            .id(comment1.getId())
            .text(comment1.getText())
            .author(userShortDto1)
            .eventId(comment1.getEvent().getId())
            .createdOn(comment1.getCreatedOn())
            .editedOn(comment1.getEditedOn())
            .build();
    private final CommentDto commentDto2 = CommentDto.builder()
            .id(comment2.getId())
            .text(comment2.getText())
            .author(userShortDto1)
            .eventId(comment2.getEvent().getId())
            .createdOn(comment2.getCreatedOn())
            .editedOn(comment2.getEditedOn())
            .build();
    private final CommentDto commentDto3 = CommentDto.builder()
            .id(comment3.getId())
            .text(comment3.getText())
            .author(userShortDto1)
            .eventId(comment3.getEvent().getId())
            .createdOn(comment3.getCreatedOn())
            .editedOn(comment3.getEditedOn())
            .build();

    @BeforeEach
    public void beforeEach() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(eventRepository.findById(event3.getId())).thenReturn(Optional.of(event3));
        when(commentMapper.toCommentDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toCommentDto(comment2)).thenReturn(commentDto2);
        when(commentMapper.toCommentDto(comment3)).thenReturn(commentDto3);
    }

    @Nested
    class GetCommentsByAdmin {
        @Test
        public void get() {
            when(commentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(comment1, comment2, comment3)));

            List<CommentDto> commentsFromService = commentService.getCommentsByAdmin(pageable);

            assertEquals(3, commentsFromService.size());

            CommentDto commentFromService1 = commentsFromService.get(0);
            CommentDto commentFromService2 = commentsFromService.get(1);
            CommentDto commentFromService3 = commentsFromService.get(2);

            assertEquals(commentDto1, commentFromService1);
            assertEquals(commentDto2, commentFromService2);
            assertEquals(commentDto3, commentFromService3);

            verify(commentRepository, times(1)).findAll(pageable);
            verify(commentMapper, times(3)).toCommentDto(any());
        }
    }

    @Nested
    class DeleteByAdmin {
        @Test
        public void delete() {
            commentService.deleteByAdmin(comment1.getId());

            verify(commentRepository, times(1)).deleteById(comment1.getId());
        }

        @Test
        public void deleteWhenIdNotFound() {
            commentService.deleteByAdmin(99L);

            verify(commentRepository, times(1)).deleteById(99L);
        }
    }

    @Nested
    class GetCommentsByPrivate {
        @Test
        public void getWhenEventNotNull() {
            when(commentRepository.findAllByAuthorIdAndEventId(user1.getId(), event1.getId()))
                    .thenReturn(List.of(comment1, comment2));

            List<CommentDto> commentsFromService = commentService.getCommentsByPrivate(user1.getId(), event1.getId(),
                    pageable);

            assertEquals(2, commentsFromService.size());

            CommentDto commentFromService1 = commentsFromService.get(0);
            CommentDto commentFromService2 = commentsFromService.get(1);

            assertEquals(commentDto1, commentFromService1);
            assertEquals(commentDto2, commentFromService2);

            verify(userRepository, times(1)).findById(any());
            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findAllByAuthorIdAndEventId(any(), any());
            verify(commentMapper, times(2)).toCommentDto(any());
        }

        @Test
        public void getAllWhenEventNotNull() {
            when(commentRepository.findAllByAuthorIdAndEventId(user1.getId(), event1.getId())).thenReturn(List.of());

            List<CommentDto> commentsFromService = commentService.getCommentsByPrivate(user1.getId(), event1.getId(),
                    pageable);

            assertTrue(commentsFromService.isEmpty());

            verify(userRepository, times(1)).findById(any());
            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findAllByAuthorIdAndEventId(any(), any());
        }

        @Test
        public void getWhenEventIsNull() {
            when(commentRepository.findAllByAuthorId(user1.getId())).thenReturn(List.of(comment1, comment2, comment3));

            List<CommentDto> commentsFromService = commentService.getCommentsByPrivate(user1.getId(), null,
                    pageable);

            assertEquals(3, commentsFromService.size());

            CommentDto commentFromService1 = commentsFromService.get(0);
            CommentDto commentFromService2 = commentsFromService.get(1);
            CommentDto commentFromService3 = commentsFromService.get(2);

            assertEquals(commentDto1, commentFromService1);
            assertEquals(commentDto2, commentFromService2);
            assertEquals(commentDto3, commentFromService3);

            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findAllByAuthorId(any());
            verify(commentMapper, times(3)).toCommentDto(any());
        }
    }

    @Nested
    class CreateByPrivate {
        @Test
        public void create() {
            when(commentRepository.save(any())).thenReturn(comment1);

            CommentDto commentFromService = commentService.createByPrivate(user1.getId(), event1.getId(), newCommentDto);

            assertEquals(commentDto1, commentFromService);

            verify(userRepository, times(1)).findById(any());
            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());
            verify(commentMapper, times(1)).toCommentDto(any());

            Comment savedComment = commentArgumentCaptor.getValue();

            assertNull(savedComment.getId());
            assertEquals(comment1.getText(), savedComment.getText());
            assertEquals(comment1.getAuthor(), savedComment.getAuthor());
            assertEquals(comment1.getEvent().getId(), savedComment.getEvent().getId());
            assertNotNull(savedComment.getCreatedOn());
            assertNull(savedComment.getEditedOn());
        }

        @Test
        public void createWhenEventNotPublished() {
            ForbiddenException exception = assertThrows(ForbiddenException.class,
                    () -> commentService.createByPrivate(user1.getId(), event3.getId(), newCommentDto));
            assertEquals("Создавать комментарии можно только к опубликованным событиям.", exception.getMessage());

            verify(userRepository, times(1)).findById(any());
            verify(eventRepository, times(1)).findById(any());
            verify(commentRepository, never()).save(any());
        }
    }

    @Nested
    class EditByPrivate {
        @Test
        public void edit() {
            when(commentRepository.findById(comment3.getId())).thenReturn(Optional.of(comment3));
            when(commentRepository.save(any())).thenReturn(comment3);

            CommentDto commentFromService = commentService.editByPrivate(user1.getId(), comment3.getId(), newCommentDtoToUpdate);

            assertEquals(commentDto3, commentFromService);

            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());
            verify(commentMapper, times(1)).toCommentDto(any());

            Comment savedComment = commentArgumentCaptor.getValue();

            assertEquals(comment3.getId(), savedComment.getId());
            assertEquals(newCommentDtoToUpdate.getText(), savedComment.getText());
            assertEquals(comment3.getAuthor(), savedComment.getAuthor());
            assertEquals(comment3.getEvent().getId(), savedComment.getEvent().getId());
            assertEquals(comment3.getCreatedOn(), savedComment.getCreatedOn());
            assertNotNull(savedComment.getEditedOn());
        }

        @Test
        public void editWhenCommentNotFound() {
            when(commentRepository.findById(comment3.getId())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> commentService.editByPrivate(user1.getId(), comment3.getId(), newCommentDtoToUpdate));
            assertEquals("Комментарий с таким id не найден.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any());
            verify(commentRepository, never()).save(any());
        }

        @Test
        public void editWhenUserNotCommentOwner() {
            when(commentRepository.findById(comment3.getId())).thenReturn(Optional.of(comment3));

            ForbiddenException exception = assertThrows(ForbiddenException.class,
                    () -> commentService.editByPrivate(user2.getId(), comment3.getId(), newCommentDtoToUpdate));
            assertEquals("Пользователь не является владельцем.", exception.getMessage());

            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findById(any());
            verify(commentRepository, never()).save(any());
        }
    }

    @Nested
    class DeleteByPrivate {
        @Test
        public void delete() {
            when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));

            commentService.deleteByPrivate(user1.getId(), comment1.getId());

            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).deleteById(any());
        }

        @Test
        public void deleteWhenCommentNotFound() {
            when(commentRepository.findById(comment1.getId())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> commentService.deleteByPrivate(user1.getId(), comment1.getId()));
            assertEquals("Комментарий с таким id не найден.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any());
            verify(commentRepository, never()).deleteById(any());
        }

        @Test
        public void deleteWhenUserNotCommentOwner() {
            when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));

            ForbiddenException exception = assertThrows(ForbiddenException.class,
                    () -> commentService.deleteByPrivate(user2.getId(), comment1.getId()));
            assertEquals("Пользователь не является владельцем.", exception.getMessage());

            verify(userRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findById(any());
            verify(commentRepository, never()).deleteById(any());
        }
    }

    @Nested
    class GetCommentsByPublic {
        @Test
        public void get() {
            when(commentRepository.findAllByEventId(event1.getId(), pageable)).thenReturn(List.of(comment1, comment2, comment3));

            List<CommentDto> commentsFromService = commentService.getCommentsByPublic(event1.getId(), pageable);

            assertEquals(3, commentsFromService.size());

            CommentDto commentFromService1 = commentsFromService.get(0);
            CommentDto commentFromService2 = commentsFromService.get(1);
            CommentDto commentFromService3 = commentsFromService.get(2);

            assertEquals(commentDto1, commentFromService1);
            assertEquals(commentDto2, commentFromService2);
            assertEquals(commentDto3, commentFromService3);

            verify(eventRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findAllByEventId(any(), any());
            verify(commentMapper, times(3)).toCommentDto(any());
        }

        @Test
        public void getEmpty() {
            when(commentRepository.findAllByEventId(event1.getId(), pageable)).thenReturn(List.of());

            List<CommentDto> commentsFromService = commentService.getCommentsByPublic(event1.getId(), pageable);

            assertTrue(commentsFromService.isEmpty());

            verify(eventRepository, times(1)).findById(any());
            verify(commentRepository, times(1)).findAllByEventId(any(), any());
        }
    }

    @Nested
    class GetCommentByPublic {
        @Test
        public void get() {
            when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));

            CommentDto commentFromService = commentService.getCommentByPublic(comment1.getId());

            assertEquals(commentDto1, commentFromService);

            verify(commentRepository, times(1)).findById(any());
            verify(commentMapper, times(1)).toCommentDto(any());
        }

        @Test
        public void getWhenCommentNotFound() {
            when(commentRepository.findById(comment1.getId())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> commentService.getCommentByPublic(comment1.getId()));
            assertEquals("Комментарий с таким id не найден.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any());
        }
    }
}
