package ru.practicum.main_service.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.comment.domain.model.Comment;
import ru.practicum.main_service.comment.domain.repository.CommentRepository;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.mapper.CommentMapper;
import ru.practicum.main_service.event.domain.model.Event;
import ru.practicum.main_service.event.domain.repository.EventRepository;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.exception.ForbiddenException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.domain.model.User;
import ru.practicum.main_service.user.domain.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentDto> getCommentsByAdmin(Pageable pageable) {
        log.info("Получение всех комментариев с пагинацией {}", pageable);

        return commentRepository.findAll(pageable).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByAdmin(Long commentId) {

        log.info("Удаление комментария с id {}", commentId);

        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getCommentsByPrivate(Long userId, Long eventId, Pageable pageable) {

        log.info("Получение всех комментариев пользователя с id {} к событию с id {} и пагинацией {}",
                userId, eventId, pageable);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден."));

        List<Comment> comments;
        if (eventId != null) {
            eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с таким id не найдено."));

            comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId);
        } else {
            comments = commentRepository.findAllByAuthorId(userId, pageable);
        }

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto) {

        log.info("Создание комментария к событию с id {} пользователем с id {} и параметрами {}",
                eventId, userId, newCommentDto);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден."));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с таким id не найдено."));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Создавать комментарии можно только к опубликованным событиям.");
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto editByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto) {

        log.info("Обновление комментария с id {} пользователем с id {} и параметрами {}", commentId, userId, newCommentDto);

        Comment commentFromRepository = getCommentById(commentId);

        checkUserIsOwner(userId, commentFromRepository.getAuthor().getId());

        commentFromRepository.setText(newCommentDto.getText());

        return commentMapper.toCommentDto(commentRepository.save(commentFromRepository));
    }

    @Transactional
    public void deleteByPrivate(Long userId, Long commentId) {

        log.info("Удаление комментария с id {} пользователем с id {}", commentId, userId);

        checkUserIsOwner(userId, getCommentById(commentId).getAuthor().getId());

        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getCommentsByPublic(Long eventId, Pageable pageable) {

        log.info("Получение всех комментариев к событию с id {} и пагинацией {}", eventId, pageable);

        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с таким id не найдено."));

        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto getCommentByPublic(Long commentId) {

        log.info("Получение комментария с id {}", commentId);

        return commentMapper.toCommentDto(getCommentById(commentId));
    }


    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с таким id не найден."));
    }

    private void checkUserIsOwner(Long id, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден."));
        if (!Objects.equals(id, userId)) {
            throw new ForbiddenException("Пользователь не является владельцем.");
        }
    }
}
