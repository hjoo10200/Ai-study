package com.example.demo.internetsearch;

import java.net.URI;
import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class InternetSearchTools {
  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final String apiKey;

  public InternetSearchTools(
      @Value("${serpapi.endpoint}") String endpoint,
      @Value("${serpapi.apiKey}") String apiKey,
      ObjectMapper objectMapper) {
    this.webClient = WebClient.builder().baseUrl(endpoint).build();
    this.apiKey = apiKey;
    this.objectMapper = objectMapper;
  }

  @Tool(description = "Google에서 인터넷을 검색하고 검색 결과의 제목, 링크, 요약을 반환합니다.")
  public String search(@ToolParam(description = "Google 검색어") String query) {
    if (query == null || query.isBlank()) {
      return "검색어를 입력해 주세요.";
    }
    try {
      String response = webClient.get()
          .uri(builder -> builder
              .queryParam("engine", "google")
              .queryParam("q", "{query}")
              .queryParam("api_key", "{apiKey}")
              .queryParam("hl", "ko")
              .build(query, apiKey))
          .retrieve()
          .bodyToMono(String.class)
          .block(Duration.ofSeconds(30));
      if (response == null || response.isBlank()) {
        return "검색 서버에서 빈 응답을 반환했습니다.";
      }
      JsonNode root = objectMapper.readTree(response);
      if (root.hasNonNull("error")) {
        return "검색에 실패했습니다. SerpApi 설정 및 사용 한도를 확인해 주세요.";
      }
      JsonNode results = root.path("organic_results");
      if (!results.isArray() || results.isEmpty()) {
        return "검색 결과가 없습니다.";
      }
      StringBuilder text = new StringBuilder();
      for (JsonNode result : results) {
        text.append("제목: ").append(result.path("title").asText()).append('\n')
            .append("링크: ").append(result.path("link").asText()).append('\n')
            .append("요약: ").append(result.path("snippet").asText()).append("\n\n");
      }
      return text.toString().strip();
    } catch (Exception e) {
      // 요청 URL과 예외 메시지에는 API 키가 포함될 수 있으므로 노출하지 않습니다.
      return "검색 요청에 실패했습니다. SerpApi 설정 및 네트워크 상태를 확인해 주세요.";
    }
  }

  @Tool(description = "검색 결과의 HTTP 또는 HTTPS URL에 접속하여 웹 페이지 본문 텍스트를 반환합니다.")
  public String fetch(@ToolParam(description = "본문을 읽을 웹 페이지의 HTTP 또는 HTTPS URL") String url) {
    if (url == null || url.isBlank()) {
      return "웹 페이지 URL을 입력해 주세요.";
    }
    try {
      URI uri = URI.create(url);
      if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
          || uri.getHost() == null) {
        return "HTTP 또는 HTTPS URL을 입력해 주세요.";
      }
      Document document = Jsoup.connect(url)
          .userAgent("Mozilla/5.0")
          .timeout(15000)
          .maxBodySize(2 * 1024 * 1024)
          .get();
      document.select("script, style, noscript, nav, header, footer").remove();
      String body = document.body().text();
      if (body.isBlank()) {
        return "웹 페이지에 읽을 수 있는 본문이 없습니다.";
      }
      return "제목: " + document.title() + "\n본문: "
          + body.substring(0, Math.min(body.length(), 20000));
    } catch (Exception e) {
      return "웹 페이지를 가져오지 못했습니다. URL 및 페이지 접근 가능 여부를 확인해 주세요.";
    }
  }
}
