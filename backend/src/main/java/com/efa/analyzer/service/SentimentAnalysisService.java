package com.efa.analyzer.service;

import com.efa.analyzer.enums.Sentiment;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SentimentAnalysisService {

  private static final Logger log = LoggerFactory.getLogger(SentimentAnalysisService.class);

  private final RestClient client;

  public SentimentAnalysisService(RestClient sentimentClient) {
    this.client = sentimentClient;
  }

  public Sentiment analyze(String text) {
    if (text == null || text.isBlank()) {
      return Sentiment.NEUTRAL;
    }

    try {
      JsonNode responseBody =
          client
              .post()
              .uri("")
              .body(Map.of("inputs", text))
              .retrieve()
              .onStatus(
                  HttpStatusCode::isError,
                  (request, response) -> {
                    throw new RuntimeException("HF error: " + response.getStatusCode());
                  })
              .body(JsonNode.class);

      return extractTopSentiment(responseBody);

    } catch (Exception ex) {
      log.error("Sentiment API call failed", ex);
      return Sentiment.NEUTRAL;
    }
  }

  private Sentiment extractTopSentiment(JsonNode responseBody) {
    if (responseBody == null || responseBody.isEmpty()) {
      return Sentiment.NEUTRAL;
    }

    JsonNode candidates =
        (responseBody.isArray() && !responseBody.isEmpty() && responseBody.get(0).isArray())
            ? responseBody.get(0)
            : responseBody.isArray() ? responseBody : null;

    if (candidates == null) {
      if (responseBody.hasNonNull("error")) {
        log.warn("HF error: {}", responseBody.get("error").asText());
      }
      return Sentiment.NEUTRAL;
    }

    double bestScore = Double.NEGATIVE_INFINITY;
    String bestLabel = null;

    for (JsonNode candidate : candidates) {
      if (candidate == null) continue;
      JsonNode scoreNode = candidate.get("score");
      JsonNode labelNode = candidate.get("label");
      if (scoreNode == null || labelNode == null) continue;

      double score = scoreNode.asDouble();
      String label = labelNode.asText();

      if (score > bestScore || (score == bestScore && isHigherPriority(label, bestLabel))) {
        bestScore = score;
        bestLabel = label;
      }
    }

    return mapLabelToSentiment(bestLabel);
  }

  private boolean isHigherPriority(String newLabel, String currentLabel) {
    if (currentLabel == null) return true;
    int newRank = labelPriorityRank(newLabel);
    int currentRank = labelPriorityRank(currentLabel);
    return newRank < currentRank;
  }

  private int labelPriorityRank(String label) {
    String normalized = label == null ? "" : label.trim().toLowerCase();
    return switch (normalized) {
      case "positive", "label_2" -> 0;
      case "neutral", "label_1" -> 1;
      case "negative", "label_0" -> 2;
      default -> 3;
    };
  }

  private Sentiment mapLabelToSentiment(String label) {
    if (label == null) return Sentiment.NEUTRAL;
    String normalized = label.trim().toLowerCase();
    return switch (normalized) {
      case "positive", "label_2" -> Sentiment.POSITIVE;
      case "negative", "label_0" -> Sentiment.NEGATIVE;
      case "neutral", "label_1" -> Sentiment.NEUTRAL;
      default -> Sentiment.NEUTRAL;
    };
  }
}
