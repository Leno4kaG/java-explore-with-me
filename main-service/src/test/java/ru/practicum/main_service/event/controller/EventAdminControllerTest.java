package ru.practicum.main_service.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main_service.Utils;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.LocationDto;
import ru.practicum.main_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main_service.event.enums.EventStateAction;
import ru.practicum.main_service.event.service.EventService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventAdminController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventAdminControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private EventService eventService;

    private final EventFullDto eventFullDto1 = EventFullDto.builder()
            .id(1L)
            .build();
    private final EventFullDto eventFullDto2 = EventFullDto.builder()
            .id(2L)
            .build();
    private final LocationDto location = LocationDto.builder()
            .lat(15.0456F)
            .lon(-18.3330F)
            .build();
    private UpdateEventAdminRequest updateEventAdminRequest;

    @Nested
    class FindEventsByAdmin {
        @Test
        public void findEventsForAdmin() throws Exception {
            when(eventService.getEventsByAdmin(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(List.of(eventFullDto1, eventFullDto2));

            mvc.perform(get("/admin/events?users=0&" +
                            "states=PUBLISHED&" +
                            "categories=0&" +
                            "rangeStart=2023-05-06 13:40:00&" +
                            "rangeEnd=2044-05-06 13:40:00&" +
                            "from=0&" +
                            "size=1000")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(eventFullDto1, eventFullDto2))));

            verify(eventService, times(1))
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWithoutParameters() throws Exception {
            when(eventService.getEventsByAdmin(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(List.of(eventFullDto1, eventFullDto2));

            mvc.perform(get("/admin/events")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(eventFullDto1, eventFullDto2))));

            verify(eventService, times(1))
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWhenEmpty() throws Exception {
            when(eventService.getEventsByAdmin(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(List.of());

            mvc.perform(get("/admin/events")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of())));

            verify(eventService, times(1))
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWhenRangeStartNoPattern() throws Exception {
            mvc.perform(get("/admin/events?rangeStart=2023-05-06T13:30:00")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never())
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWhenRangeEndNoPattern() throws Exception {
            mvc.perform(get("/admin/events?rangeEnd=2047-05-06T13:30:00")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never())
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWhenFromIsNegative() throws Exception {
            mvc.perform(get("/admin/events?from=-1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never())
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWhenSizeIsZero() throws Exception {
            mvc.perform(get("/admin/events?size=0")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never())
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        public void findEventsForAdminWhenSizeIsNegative() throws Exception {
            mvc.perform(get("/admin/events?size=-1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never())
                    .getEventsByAdmin(any(), any(), any(), any(), any(), any(), any());
        }
    }

    @Nested
    class EditEventForAdmin {
        @BeforeEach
        public void beforeEach() {
            updateEventAdminRequest = UpdateEventAdminRequest.builder()
                    .annotation("edit test annotation event")
                    .title("title")
                    .category(1L)
                    .description("edit test description event")
                    .eventDate(LocalDateTime.parse("2023-06-06 16:30:00", Utils.DATE_FORMATTER))
                    .location(location)
                    .paid(true)
                    .participantLimit(0)
                    .requestModeration(false)
                    .stateAction(EventStateAction.PUBLISH_EVENT)
                    .build();
        }

        @Test
        public void edit() throws Exception {
            when(eventService.editEventByAdmin(any(), any())).thenReturn(eventFullDto1);

            mvc.perform(patch("/admin/events/1")
                            .content(mapper.writeValueAsString(updateEventAdminRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).editEventByAdmin(any(), any());
        }

        @Test
        public void editWhenUpdateEventAdminRequestIsEmpty() throws Exception {
            updateEventAdminRequest = new UpdateEventAdminRequest();

            when(eventService.editEventByAdmin(any(), any())).thenReturn(eventFullDto1);

            mvc.perform(patch("/admin/events/1")
                            .content(mapper.writeValueAsString(updateEventAdminRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).editEventByAdmin(any(), any());
        }

        @Test
        public void editWhenAnnotationLessMin() throws Exception {
            updateEventAdminRequest.setAnnotation("ar");

            mvc.perform(patch("/admin/events/1")
                            .content(mapper.writeValueAsString(updateEventAdminRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByAdmin(any(), any());
        }


        @Test
        public void editWhenLocationLatIsNull() throws Exception {
            LocationDto newLocationDto = LocationDto.builder()
                    .lat(null)
                    .lon(1.177F)
                    .build();
            updateEventAdminRequest.setLocation(newLocationDto);

            mvc.perform(patch("/admin/events/1")
                            .content(mapper.writeValueAsString(updateEventAdminRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByAdmin(any(), any());
        }

        @Test
        public void editWhenLocationLonIsNull() throws Exception {
            LocationDto newLocationDto = LocationDto.builder()
                    .lat(1.177F)
                    .lon(null)
                    .build();
            updateEventAdminRequest.setLocation(newLocationDto);

            mvc.perform(patch("/admin/events/1")
                            .content(mapper.writeValueAsString(updateEventAdminRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByAdmin(any(), any());
        }

        @Test
        public void editWhenParticipantLimitIsNegative() throws Exception {
            updateEventAdminRequest.setParticipantLimit(-1);

            mvc.perform(patch("/admin/events/1")
                            .content(mapper.writeValueAsString(updateEventAdminRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByAdmin(any(), any());
        }
    }
}
