package com.efa.analyzer.service;

import com.efa.analyzer.enums.Sentiment;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SentimentAnalysisService {

  private static final Logger log = LoggerFactory.getLogger(SentimentAnalysisService.class);

  private final WebClient webClient;
  private final String apiUrl;
  private final String apiToken;

  public SentimentAnalysisService(
      WebClient.Builder webClientBuilder,
      @Value("${huggingface.api.url}") String apiUrl,
      @Value("${huggingface.api.token:}") String apiToken) {
    this.apiUrl = apiUrl;
    this.apiToken = apiToken;

    WebClient.Builder b =
        webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    if (apiToken != null && !apiToken.isBlank()) {
      b.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken);
    }

    this.webClient = b.build();

    LoggerFactory.getLogger(SentimentAnalysisService.class)
        .info("HF url={}, tokenPresent={}", apiUrl, (apiToken != null && !apiToken.isBlank()));
  }

  public Sentiment analyzeSentiment(String text) {
    if (text == null || text.isBlank()) {
      return Sentiment.NEUTRAL;
    }
    if (apiToken == null || apiToken.isBlank()) {
      log.warn("Hugging Face token is empty; returning NEUTRAL");
      return Sentiment.NEUTRAL;
    }

    try {
      JsonNode response =
          webClient
              .post()
              .uri(apiUrl)
              .bodyValue(new SentimentRequest(text)) // {"inputs": "..."}
              .retrieve()
              .bodyToMono(JsonNode.class)
              .timeout(Duration.ofSeconds(10))
              .onErrorResume(
                  err -> {
                    log.error("Hugging Face API error: {}", err.getMessage());
                    return Mono.empty();
                  })
              .block();

      return parseSentimentResponse(response);
    } catch (Exception e) {
      log.error("Failed to analyze sentiment", e);
      return Sentiment.NEUTRAL;
    }
  }

  /** Supports both nested ([[{label,score}...]]) and flat ([{label,score}...]) HF formats. */
  private Sentiment parseSentimentResponse(JsonNode response) {
    if (response == null || response.isEmpty()) return Sentiment.NEUTRAL;

    JsonNode candidates;
    if (response.isArray() && response.get(0) != null && response.get(0).isArray()) {
      candidates = response.get(0);
    } else if (response.isArray()) {
      candidates = response;
    } else {
      if (response.hasNonNull("error")) {
        log.warn("HF returned error: {}", response.get("error").asText());
      }
      return Sentiment.NEUTRAL;
    }

    double best = -1.0;
    String topLabel = null;

    for (JsonNode p : candidates) {
      if (p == null) continue;
      JsonNode s = p.get("score");
      JsonNode l = p.get("label");
      if (s == null || l == null) continue;
      double sc = s.asDouble();
      if (sc > best) {
        best = sc;
        topLabel = l.asText();
      }
    }
    return mapLabelToSentiment(topLabel);
  }

  private Sentiment mapLabelToSentiment(String label) {
    if (label == null) return Sentiment.NEUTRAL;
    String l = label.toLowerCase();
    return switch (l) {
      case "positive", "label_2" -> Sentiment.POSITIVE;
      case "neutral", "label_1" -> Sentiment.NEUTRAL;
      case "negative", "label_0" -> Sentiment.NEGATIVE;
      default -> Sentiment.NEUTRAL;
    };
  }

  private record SentimentRequest(String inputs) {}
}
