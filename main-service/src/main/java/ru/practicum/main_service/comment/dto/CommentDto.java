package ru.practicum.main_service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main_service.Utils;
import ru.practicum.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
   private Long id;
   private String text;
   private UserShortDto author;
   private Long eventId;
    @JsonFormat(pattern = Utils.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
   private LocalDateTime createdIn;
    @JsonFormat(pattern = Utils.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
   private LocalDateTime editedIn;
}
