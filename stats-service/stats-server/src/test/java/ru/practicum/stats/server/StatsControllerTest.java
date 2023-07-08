package ru.practicum.stats.server;

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
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.Utils;
import ru.practicum.stats.server.controller.StatsController;
import ru.practicum.stats.server.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private StatsService statsService;

    private EndpointHit endpointHit;
    private String start;
    private String end;
    private List<String> uris;
    private Boolean unique;

    @Nested
    class AddHit {
        @BeforeEach
        public void beforeEach() {
            endpointHit = EndpointHit.builder()
                    .app("APP")
                    .uri("/test/uri/1")
                    .ip("127.0.0.1")
                    .timestamp(LocalDateTime.parse("2023-01-06 10:00:00", Utils.DATE_FORMATTER))
                    .build();
        }

        @Test
        public void save() throws Exception {
            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            verify(statsService, times(1)).save(ArgumentMatchers.eq(endpointHit));
        }

        @Test
        public void saveErrorWhenAppIsNullOrEmpty() throws Exception {
            endpointHit.setApp(null);

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());

            endpointHit.setApp("");

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());

            endpointHit.setApp(" ");

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());
        }


        @Test
        public void saveErrorIfUriIsNullOrEmpty() throws Exception {
            endpointHit.setUri(null);

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());

            endpointHit.setUri("");

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());

            endpointHit.setUri(" ");

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());
        }


        @Test
        public void saveIsErrorWHenIpIsNullOrEmpty() throws Exception {
            endpointHit.setIp(null);

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());

            endpointHit.setIp("");

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());

            endpointHit.setIp(" ");

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());
        }

        @Test
        public void saveIsErrorWhenTimestampIsNullOrEmpty() throws Exception {
            endpointHit.setTimestamp(null);

            mvc.perform(post(Utils.HIT)
                            .content(mapper.writeValueAsString(endpointHit))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).save(any());
        }
    }

    @Nested
    class GetStats {
        @BeforeEach
        public void beforeEach() {
            start = "2020-01-01 00:00:00";
            end = "2035-01-01 00:00:00";
            uris = List.of("/test/uri/1", "/test/uri/2");
            unique = true;
        }

        @Test
        public void getAll() throws Exception {
            mvc.perform(get(Utils.STATS + "?start={start}&end={end}&uris={uris}&uris={uris}&unique={unique}",
                            start, end, uris.get(0), uris.get(1), unique)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(statsService, times(1)).getAllStats(any(), any(),
                    ArgumentMatchers.eq(uris), ArgumentMatchers.eq(unique));
        }

        @Test
        public void getAllWhenDefaultUnique() throws Exception {
            mvc.perform(get(Utils.STATS + "?start={start}&end={end}&uris={uris}&uris={uris}",
                            start, end, uris.get(0), uris.get(1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(statsService, times(1)).getAllStats(any(), any(),
                    ArgumentMatchers.eq(uris), ArgumentMatchers.eq(false));
        }

        @Test
        public void getAllWhenUrisIsNotExist() throws Exception {
            mvc.perform(get(Utils.STATS + "?start={start}&end={end}&unique={unique}",
                            start, end, unique)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(statsService, times(1)).getAllStats(any(), any(),
                    ArgumentMatchers.eq(null), ArgumentMatchers.eq(unique));
        }

        @Test
        public void getAllWhenUrisAndUniqueAreNotExist() throws Exception {
            mvc.perform(get(Utils.STATS + "?start={start}&end={end}",
                            start, end, unique)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(statsService, times(1)).getAllStats(any(), any(),
                    ArgumentMatchers.eq(null), ArgumentMatchers.eq(false));
        }

        @Test
        public void getAllErrorWhenStartOrEndNotValid() throws Exception {
            start = "2023-07-01T00:00:00";

            mvc.perform(get(Utils.STATS + "?start={start}&end={end}&uris={uris}&uris={uris}&unique={unique}",
                            start, end, uris.get(0), uris.get(1), unique)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).getAllStats(any(LocalDateTime.class), any(LocalDateTime.class),
                    any(), any(Boolean.class));

            end = "2023-07-01T00:00:00";

            mvc.perform(get(Utils.STATS + "?start={start}&end={end}&uris={uris}&uris={uris}&unique={unique}",
                            start, end, uris.get(0), uris.get(1), unique)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(statsService, never()).getAllStats(any(LocalDateTime.class), any(LocalDateTime.class),
                    any(), any(Boolean.class));
        }
    }
}
