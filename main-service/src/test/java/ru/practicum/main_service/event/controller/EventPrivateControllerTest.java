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
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.dto.LocationDto;
import ru.practicum.main_service.event.dto.NewEventDto;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.dto.UpdateEventUserRequest;
import ru.practicum.main_service.event.enums.EventStateAction;
import ru.practicum.main_service.event.enums.RequestStatusAction;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.event.service.RequestService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventPrivateController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventPrivateControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private RequestService requestService;

    private final EventShortDto eventShortDto1 = EventShortDto.builder()
            .id(1L)
            .build();
    private final EventShortDto eventShortDto2 = EventShortDto.builder()
            .id(2L)
            .build();
    private final EventFullDto eventFullDto1 = EventFullDto.builder()
            .id(eventShortDto1.getId())
            .build();
    private final LocationDto location = LocationDto.builder()
            .lat(19.0156F)
            .lon(-18.6789F)
            .build();
    private final ParticipationRequestDto participationRequestDto1 = ParticipationRequestDto.builder()
            .id(1L)
            .build();
    private final ParticipationRequestDto participationRequestDto2 = ParticipationRequestDto.builder()
            .id(2L)
            .build();
    private final EventRequestStatusUpdateResult eventRequestStatusUpdateResult = EventRequestStatusUpdateResult.builder()
            .confirmedRequests(List.of(participationRequestDto1, participationRequestDto2))
            .rejectedRequests(List.of())
            .build();
    private NewEventDto newEventDto1;
    private UpdateEventUserRequest updateEventUserRequest;
    private EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest;

    @Nested
    class GetAllEventsByPrivate {
        @Test
        public void getAllEvents() throws Exception {
            when(eventService.getAllEventsByPrivate(any(), any())).thenReturn(List.of(eventShortDto1, eventShortDto2));

            mvc.perform(get("/users/1/events?from=0&size=100")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(eventShortDto1, eventShortDto2))));

            verify(eventService, times(1)).getAllEventsByPrivate(any(), any());
        }

        @Test
        public void getAllEventsByDefault() throws Exception {
            when(eventService.getAllEventsByPrivate(any(), any())).thenReturn(List.of(eventShortDto1, eventShortDto2));

            mvc.perform(get("/users/1/events")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(eventShortDto1, eventShortDto2))));

            verify(eventService, times(1)).getAllEventsByPrivate(any(), any());
        }

        @Test
        public void getAllEventsWhenEmpty() throws Exception {
            when(eventService.getAllEventsByPrivate(any(), any())).thenReturn(List.of());

            mvc.perform(get("/users/1/events")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of())));

            verify(eventService, times(1)).getAllEventsByPrivate(any(), any());
        }

        @Test
        public void getAllEventsWhenFromIsNegative() throws Exception {
            mvc.perform(get("/users/1/events?from=-1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).getAllEventsByPrivate(any(), any());
        }

        @Test
        public void getAllEventsWhenSizeIsZero() throws Exception {
            mvc.perform(get("/users/1/events?size=0")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).getAllEventsByPrivate(any(), any());
        }

        @Test
        public void getAllEventsWhenSizeIsNegative() throws Exception {
            mvc.perform(get("/users/1/events?size=-1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).getAllEventsByPrivate(any(), any());
        }
    }

    @Nested
    class CreateEventByPrivate {
        @BeforeEach
        public void beforeEach() {
            newEventDto1 = NewEventDto.builder()
                    .annotation("new test annotation event")
                    .title("title")
                    .category(1L)
                    .description("new test description event")
                    .eventDate(LocalDateTime.parse("2023-05-06 17:30:00", Utils.DATE_FORMATTER))
                    .location(location)
                    .paid(true)
                    .participantLimit(0)
                    .requestModeration(false)
                    .build();
        }

        @Test
        public void create() throws Exception {
            when(eventService.createEventByPrivate(any(), any())).thenReturn(eventFullDto1);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenAnnotationIsNull() throws Exception {
            newEventDto1.setAnnotation(null);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenAnnotationIsEmpty() throws Exception {
            newEventDto1.setAnnotation("");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenAnnotationIsBlank() throws Exception {

            newEventDto1.setAnnotation("  ");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenAnnotationLessMin() throws Exception {

            newEventDto1.setAnnotation("rt");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenDescriptionIsNull() throws Exception {
            newEventDto1.setDescription(null);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenDescriptionIsEmpty() throws Exception {
            newEventDto1.setDescription("");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenDescriptionIsBlank() throws Exception {
            newEventDto1.setDescription(" ");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenDescriptionLessMin() throws Exception {

            newEventDto1.setDescription("yu");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenTitleIsNull() throws Exception {
            newEventDto1.setTitle(null);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenTitleIsEmpty() throws Exception {
            newEventDto1.setTitle("");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenTitleIsBlank() throws Exception {

            newEventDto1.setTitle(" ");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenTitleLessMin() throws Exception {

            newEventDto1.setTitle("ty");

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }


        @Test
        public void createWhenCategoryIsNull() throws Exception {
            newEventDto1.setCategory(null);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenEventDateIsNull() throws Exception {
            newEventDto1.setEventDate(null);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenParticipantLimitIsNull() throws Exception {
            newEventDto1.setParticipantLimit(null);

            when(eventService.createEventByPrivate(any(), any())).thenReturn(eventFullDto1);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenParticipantLimitIsNegative() throws Exception {
            newEventDto1.setParticipantLimit(-1);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenLocationIsNull() throws Exception {
            newEventDto1.setLocation(null);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenLocationLonIsNull() throws Exception {
            LocationDto newLocationDto = LocationDto.builder()
                    .lat(14.1254F)
                    .lon(null)
                    .build();
            newEventDto1.setLocation(newLocationDto);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }

        @Test
        public void createWhenLocationLatIsNull() throws Exception {
            LocationDto newLocationDto = LocationDto.builder()
                    .lat(null)
                    .lon(14.1254F)
                    .build();
            newEventDto1.setLocation(newLocationDto);

            mvc.perform(post("/users/1/events")
                            .content(mapper.writeValueAsString(newEventDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).createEventByPrivate(any(), any());
        }
    }

    @Nested
    class GetEventByPrivate {
        @Test
        public void getEventByPrivate() throws Exception {
            when(eventService.getEventByPrivate(any(), any())).thenReturn(eventFullDto1);

            mvc.perform(get("/users/1/events/1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).getEventByPrivate(any(), any());
        }
    }

    @Nested
    class EditEventByPrivate {
        @BeforeEach
        public void beforeEach() {
            updateEventUserRequest = UpdateEventUserRequest.builder()
                    .annotation("edit test annotation event")
                    .title("title")
                    .category(1L)
                    .description("edit test description event")
                    .eventDate(LocalDateTime.parse("2023-05-06 17:30:00", Utils.DATE_FORMATTER))
                    .location(location)
                    .paid(true)
                    .participantLimit(0)
                    .requestModeration(false)
                    .stateAction(EventStateAction.SEND_TO_REVIEW)
                    .build();
        }

        @Test
        public void editEventByPrivate() throws Exception {
            when(eventService.editEventByPrivate(any(), any(), any())).thenReturn(eventFullDto1);

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).editEventByPrivate(any(), any(), any());
        }

        @Test
        public void editEventByPrivateWhenUpdateEventUserRequestIsEmpty() throws Exception {
            updateEventUserRequest = new UpdateEventUserRequest();

            when(eventService.editEventByPrivate(any(), any(), any())).thenReturn(eventFullDto1);

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(eventFullDto1)));

            verify(eventService, times(1)).editEventByPrivate(any(), any(), any());
        }

        @Test
        public void editEventByPrivateWhenAnnotationLessMin() throws Exception {

            updateEventUserRequest.setAnnotation("we");

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByPrivate(any(), any(), any());
        }


        @Test
        public void editEventByPrivateWhenDescriptionLessMin() throws Exception {
            updateEventUserRequest.setDescription("rty");

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByPrivate(any(), any(), any());
        }

        @Test
        public void editEventByPrivateWhenTitleLessMin() throws Exception {

            updateEventUserRequest.setTitle("ty");

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByPrivate(any(), any(), any());
        }


        @Test
        public void editEventByPrivateWhenParticipantLimitIsNegative() throws Exception {
            updateEventUserRequest.setParticipantLimit(-1);

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByPrivate(any(), any(), any());
        }

        @Test
        public void editEventByPrivateWhenLocationLatIsNull() throws Exception {
            LocationDto newLocationDto = LocationDto.builder()
                    .lat(null)
                    .lon(17.1288F)
                    .build();
            updateEventUserRequest.setLocation(newLocationDto);

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByPrivate(any(), any(), any());
        }

        @Test
        public void editEventByPrivateWhenLocationLonIsNull() throws Exception {
            LocationDto newLocationDto = LocationDto.builder()
                    .lat(16.1289F)
                    .lon(null)
                    .build();
            updateEventUserRequest.setLocation(newLocationDto);

            mvc.perform(patch("/users/1/events/1")
                            .content(mapper.writeValueAsString(updateEventUserRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(eventService, never()).editEventByPrivate(any(), any(), any());
        }
    }

    @Nested
    class GetEventRequestsByEventOwner {
        @Test
        public void getEventRequestsByEventOwner() throws Exception {
            when(requestService.getEventRequestsByEventOwner(any(), any()))
                    .thenReturn(List.of(participationRequestDto1, participationRequestDto2));

            mvc.perform(get("/users/1/events/1/requests")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(
                            List.of(participationRequestDto1, participationRequestDto2))));

            verify(requestService, times(1)).getEventRequestsByEventOwner(any(), any());
        }

        @Test
        public void getEventRequestsByEventOwnerWhenEmpty() throws Exception {
            when(requestService.getEventRequestsByEventOwner(any(), any())).thenReturn(List.of());

            mvc.perform(get("/users/1/events/1/requests")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of())));

            verify(requestService, times(1)).getEventRequestsByEventOwner(any(), any());
        }
    }

    @Nested
    class EditEventRequestsByEventOwner {
        @BeforeEach
        public void beforeEach() {
            eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                    .requestIds(List.of(1L, 2L))
                    .status(RequestStatusAction.CONFIRMED)
                    .build();
        }

        @Test
        public void edit() throws Exception {
            when(requestService.editEventRequestsByEventOwner(any(), any(), any())).thenReturn(eventRequestStatusUpdateResult);

            mvc.perform(patch("/users/1/events/1/requests")
                            .content(mapper.writeValueAsString(eventRequestStatusUpdateRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(eventRequestStatusUpdateResult)));

            verify(requestService, times(1)).editEventRequestsByEventOwner(any(), any(), any());
        }

        @Test
        public void editWhenRequestIdsIsNull() throws Exception {
            eventRequestStatusUpdateRequest.setRequestIds(null);

            mvc.perform(patch("/users/1/events/1/requests")
                            .content(mapper.writeValueAsString(eventRequestStatusUpdateRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(requestService, never()).editEventRequestsByEventOwner(any(), any(), any());
        }

        @Test
        public void editWhenRequestIdsIsEmpty() throws Exception {
            eventRequestStatusUpdateRequest.setRequestIds(List.of());

            mvc.perform(patch("/users/1/events/1/requests")
                            .content(mapper.writeValueAsString(eventRequestStatusUpdateRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(requestService, never()).editEventRequestsByEventOwner(any(), any(), any());
        }

        @Test
        public void editWhenStatusActionIsNull() throws Exception {
            eventRequestStatusUpdateRequest.setStatus(null);

            mvc.perform(patch("/users/1/events/1/requests")
                            .content(mapper.writeValueAsString(eventRequestStatusUpdateRequest))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(requestService, never()).editEventRequestsByEventOwner(any(), any(), any());
        }
    }
}
