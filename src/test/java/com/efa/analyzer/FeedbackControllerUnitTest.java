package com.efa.analyzer;

import com.efa.analyzer.controller.FeedbackController;
import com.efa.analyzer.dto.FeedbackCreateRequest;
import com.efa.analyzer.dto.FeedbackResponse;
import com.efa.analyzer.enums.Sentiment;
import com.efa.analyzer.service.feedbackService.FeedbackCommandService;
import com.efa.analyzer.service.feedbackService.FeedbackQueryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerUnitTest {

    private MockMvc mvc;

    @Mock private FeedbackCommandService feedbackCommandService;
    @Mock private FeedbackQueryService feedbackQueryService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mvc = MockMvcBuilders
                .standaloneSetup(new FeedbackController(feedbackCommandService, feedbackQueryService))
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/events/{eventId}/feedback -> 201 returns FeedbackResponse JSON and calls service with DTO")
    void post_feedback_create_201_returnsJson() throws Exception {
        OffsetDateTime createdAt = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        when(feedbackCommandService.create(eq(42), any(FeedbackCreateRequest.class)))
                .thenReturn(new FeedbackResponse(
                        555, 42, "Amazing workshop!", Sentiment.POSITIVE, createdAt));

        String body = """
          {"content":"Amazing workshop!"}
        """;

        mvc.perform(post("/api/events/{eventId}/feedback", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(555))
                .andExpect(jsonPath("$.eventId").value(42))
                .andExpect(jsonPath("$.content").value("Amazing workshop!"))
                .andExpect(jsonPath("$.sentiment").value("POSITIVE"))
                .andExpect(jsonPath("$.createdAt").exists());

        // verify DTO forwarded
        ArgumentCaptor<FeedbackCreateRequest> captor = ArgumentCaptor.forClass(FeedbackCreateRequest.class);
        verify(feedbackCommandService).create(eq(42), captor.capture());
        assertEquals("Amazing workshop!", captor.getValue().content());
    }

    @Test
    @DisplayName("POST /api/events/{eventId}/feedback with blank content -> 400 and no service call")
    void post_feedback_400_whenBlankContent() throws Exception {
        String badBody = """
          {"content":"   "}
        """;

        mvc.perform(post("/api/events/{eventId}/feedback", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(feedbackCommandService);
    }

    @Test
    @DisplayName("GET /api/events/{eventId}/feedback -> 200 returns array with sentiments")
    void get_feedback_list_for_event_200() throws Exception {
        OffsetDateTime t = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        when(feedbackQueryService.getAll(9)).thenReturn(List.of(
                new FeedbackResponse(1, 9, "Great", Sentiment.POSITIVE, t),
                new FeedbackResponse(2, 9, "Okay",  Sentiment.NEUTRAL,  t),
                new FeedbackResponse(3, 9, "Bad",   Sentiment.NEGATIVE, t)
        ));

        mvc.perform(get("/api/events/{eventId}/feedback", 9))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventId").value(9))
                .andExpect(jsonPath("$[0].sentiment").value("POSITIVE"))
                .andExpect(jsonPath("$[2].sentiment").value("NEGATIVE"));
    }

    @Test
    @DisplayName("GET /api/events/{eventId}/feedback/{feedbackId} -> 200 returns single feedback item")
    void get_feedback_by_id_200() throws Exception {
        OffsetDateTime t = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        when(feedbackQueryService.getOne(5, 77))
                .thenReturn(new FeedbackResponse(77, 5, "Loved it", Sentiment.POSITIVE, t));

        mvc.perform(get("/api/events/{eventId}/feedback/{feedbackId}", 5, 77))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(77))
                .andExpect(jsonPath("$.eventId").value(5))
                .andExpect(jsonPath("$.content").value("Loved it"))
                .andExpect(jsonPath("$.sentiment").value("POSITIVE"));
    }
}
