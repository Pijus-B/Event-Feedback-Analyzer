package com.efa.analyzer;

import com.efa.analyzer.controller.EventController;
import com.efa.analyzer.dto.EventCreateRequest;
import com.efa.analyzer.dto.EventResponse;
import com.efa.analyzer.dto.EventSummaryResponse;
import com.efa.analyzer.service.eventService.EventCommandService;
import com.efa.analyzer.service.eventService.EventQueryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerUnitTest {

    private MockMvc mvc;

    @Mock private EventCommandService eventCommandService;
    @Mock private EventQueryService eventQueryService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mvc = MockMvcBuilders.standaloneSetup(
                        new EventController(eventCommandService, eventQueryService))
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/events -> 201 returns id")
    void post_create_201_returnsId() throws Exception {
        when(eventCommandService.create(any(EventCreateRequest.class))).thenReturn(123);

        String body = """
          {"title":"Conf 2025","description":"Annual"}
        """;

        String content = mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals("123", content);
    }

    @Test
    @DisplayName("POST /api/events with blank title -> 400")
    void post_create_400_whenBlankTitle() throws Exception {
        String badBody = """
          {"title":"   ","description":"x"}
        """;

        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventCommandService);
    }

    @Test
    @DisplayName("GET /api/events -> 200 with array")
    void get_list_200_withArray() throws Exception {
        OffsetDateTime t = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        when(eventQueryService.getAll()).thenReturn(List.of(
                new EventResponse(1, "A", "a", t),
                new EventResponse(2, "B", "b", t)
        ));

        mvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].title").value("B"));
    }

    @Test
    @DisplayName("GET /api/events/by-title/{title} -> 200 with array (duplicates allowed)")
    void get_byTitle_200_withArray() throws Exception {
        OffsetDateTime t = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        when(eventQueryService.getAllByTitle("Hackathon"))
                .thenReturn(List.of(
                        new EventResponse(10, "Hackathon", "desc1", t),
                        new EventResponse(11, "Hackathon", "desc2", t)
                ));

        mvc.perform(get("/api/events/by-title/{title}", "Hackathon"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Hackathon"))
                .andExpect(jsonPath("$[1].title").value("Hackathon"));
    }

    @Test
    @DisplayName("GET /api/events/{id} -> 200 item")
    void get_one_200() throws Exception {
        OffsetDateTime t = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        when(eventQueryService.getOne(7))
                .thenReturn(new EventResponse(7, "T7", "d", t));

        mvc.perform(get("/api/events/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("T7"));
    }

    @Test
    @DisplayName("GET /api/events/{id}/summary -> 200 summary")
    void get_summary_200() throws Exception {
        when(eventQueryService.getSummary(5))
                .thenReturn(new EventSummaryResponse(5, 10, 6, 3, 1));

        mvc.perform(get("/api/events/{id}/summary", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId").value(5))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.positive").value(6))
                .andExpect(jsonPath("$.neutral").value(3))
                .andExpect(jsonPath("$.negative").value(1));
    }
}
