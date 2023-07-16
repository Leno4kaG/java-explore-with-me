package ru.practicum.main_service.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main_service.comment.controller.CommentPrivateController;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.service.CommentService;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.user.dto.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentPrivateController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentPrivateControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private CommentService commentService;

    private final UserShortDto userShortDto = UserShortDto.builder()
            .id(1L)
            .name("test name")
            .build();
    private final Event event = Event.builder()
            .id(1L)
            .build();
    private final CommentDto commentDto1 = CommentDto.builder()
            .id(1L)
            .text("test text 1")
            .author(userShortDto)
            .eventId(event.getId())
            .createdOn(LocalDateTime.now().minusHours(2))
            .editedOn(LocalDateTime.now().minusHours(1))
            .build();
    private final CommentDto commentDto2 = CommentDto.builder()
            .id(2L)
            .text("test text 2")
            .author(userShortDto)
            .eventId(event.getId())
            .createdOn(LocalDateTime.now().minusHours(2))
            .editedOn(null)
            .build();
    private final CommentDto updatedCommentDto1 = CommentDto.builder()
            .id(commentDto1.getId())
            .text("updated test text 1")
            .author(commentDto1.getAuthor())
            .eventId(commentDto1.getEventId())
            .createdOn(commentDto1.getCreatedOn())
            .editedOn(commentDto1.getEditedOn())
            .build();
    private NewCommentDto newCommentDto;

    @Nested
    class CreateByPrivate {
        @BeforeEach
        public void beforeEach() {
            newCommentDto = NewCommentDto.builder()
                    .text(commentDto1.getText())
                    .build();
        }

        @Test
        public void create() throws Exception {
            when(commentService.createByPrivate(any(), any(), any())).thenReturn(commentDto1);

            mvc.perform(post("/users/1/comments?eventId=1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(mapper.writeValueAsString(commentDto1)));

            verify(commentService, times(1)).createByPrivate(any(), any(), any());
        }

        @Test
        public void createWhenTextIsNullOrEmpty() throws Exception {
            newCommentDto.setText(null);

            mvc.perform(post("/users/1/comments?eventId=1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).createByPrivate(any(), any(), any());

            newCommentDto.setText("");

            mvc.perform(post("/users/1/comments?eventId=1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).createByPrivate(any(), any(), any());
        }
    }

    @Nested
    class EditByPrivate {
        @BeforeEach
        public void beforeEach() {
            newCommentDto = NewCommentDto.builder()
                    .text(commentDto1.getText())
                    .build();
        }

        @Test
        public void edit() throws Exception {
            when(commentService.editByPrivate(any(), any(), any())).thenReturn(updatedCommentDto1);

            mvc.perform(patch("/users/1/comments/1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(updatedCommentDto1)));

            verify(commentService, times(1)).editByPrivate(any(), any(), any());
        }

        @Test
        public void editWhenTextIsNullOrEmpty() throws Exception {
            newCommentDto.setText(null);

            mvc.perform(patch("/users/1/comments/1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).editByPrivate(any(), any(), any());

            newCommentDto.setText("");

            mvc.perform(patch("/users/1/comments/1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).editByPrivate(any(), any(), any());
        }

    }

    @Nested
    class DeleteByPrivate {
        @Test
        public void deleteTest() throws Exception {
            mvc.perform(delete("/users/1/comments/1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(commentService, times(1)).deleteByPrivate(any(), any());
        }
    }

    @Nested
    class GetCommentsByPrivate {
        @Test
        public void getComments() throws Exception {
            when(commentService.getCommentsByPrivate(any(), ArgumentMatchers.eq(null), any())).thenReturn(List.of(commentDto1, commentDto2));

            mvc.perform(get("/users/1/comments?from=0&size=10")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(commentDto1, commentDto2))));

            verify(commentService, times(1)).getCommentsByPrivate(any(), any(), any());
        }

        @Test
        public void getDefault() throws Exception {
            when(commentService.getCommentsByPrivate(any(), ArgumentMatchers.eq(null), any())).thenReturn(List.of(commentDto1, commentDto2));

            mvc.perform(get("/users/1/comments")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(commentDto1, commentDto2))));

            verify(commentService, times(1)).getCommentsByPrivate(any(), any(), any());
        }

        @Test
        public void getByEventId() throws Exception {
            when(commentService.getCommentsByPrivate(any(), any(), any())).thenReturn(List.of(commentDto1, commentDto2));

            mvc.perform(get("/users/1/comments?eventId=1&from=0&size=10")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(commentDto1, commentDto2))));

            verify(commentService, times(1)).getCommentsByPrivate(any(), any(), any());
        }

        @Test
        public void getDefaultByEventId() throws Exception {
            when(commentService.getCommentsByPrivate(any(), any(), any())).thenReturn(List.of(commentDto1, commentDto2));

            mvc.perform(get("/users/1/comments?eventId=1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(commentDto1, commentDto2))));

            verify(commentService, times(1)).getCommentsByPrivate(any(), any(), any());
        }

        @Test
        public void getWhenFromIsNegative() throws Exception {
            mvc.perform(get("/users/1/comments?from=-1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).getCommentsByPrivate(any(), any(), any());
        }

        @Test
        public void getWhenSizeIsZeroOrNegative() throws Exception {
            mvc.perform(get("/users/1/comments?size=0")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).getCommentsByPrivate(any(), any(), any());

            mvc.perform(get("/users/1/comments?size=-1")
                            .content(mapper.writeValueAsString(newCommentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).getCommentsByPrivate(any(), any(), any());
        }
    }
}
