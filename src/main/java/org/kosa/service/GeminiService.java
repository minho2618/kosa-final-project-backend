package org.kosa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.kosa.dto.recipe.RecipeListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent?key=";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecipeListResponse generateRecipes(String ingredients) {
        // JSON만 반환하도록 강하게 지시
        String prompt = """
      아래 재료로 만들 수 있는 레시피 3개를 생성하세요.
      오직 JSON만 출력하고, 설명/코드블록은 금지합니다.

      JSON 스키마:
      {
        "recipes": [
          {
            "id": "string",
            "title": "string",
            "thumbnail": "string",
            "tags": ["string"],
            "timeMinutes": 20,
            "difficulty": "쉬움" | "보통" | "어려움",
            "ingredients": [{ "name": "string", "qty": "string", "productId": "string" }],
            "steps": ["string"]
          }
        ]
      }

      제약:
      - difficulty는 "쉬움" | "보통" | "어려움" 중 하나.
      - productId는 모르면 생략하거나 null.
      - 코드블록(```json 등)과 추가 설명 금지.

      재료: %s
      """.formatted(ingredients);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        // ✅ RestTemplate에 타임아웃 설정
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5_000); // 5s
        f.setReadTimeout(30_000);   // 30s
        RestTemplate rt = new RestTemplate(f);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        final String url = GEMINI_API_URL + apiKey;

        // ✅ ⬇⬇ 이 부분이 '재시도 루프' 입니다 (여기로 교체)
        ResponseEntity<Map> res = null;
        int[] delaysMs = {1000, 2000, 4000, 8000}; // 지수 백오프 + 지터
        for (int attempt = 0; attempt <= delaysMs.length; attempt++) {
            try {
                res = rt.exchange(url, HttpMethod.POST, req, Map.class);
                break; // 성공 시 탈출
            } catch (HttpServerErrorException.ServiceUnavailable | ResourceAccessException e) {
                // 503(Service Unavailable) 또는 네트워크 타임아웃/연결실패에만 재시도
                if (attempt == delaysMs.length) {
                    // 마지막 시도도 실패면 그대로 예외 처리 or 기본 응답 리턴
                    // 여기서는 기본 응답으로 처리
                    return new RecipeListResponse(List.of());
                }
                long jitter = ThreadLocalRandom.current().nextLong(200); // 0~199ms
                try {
                    Thread.sleep(delaysMs[attempt] + jitter);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new RecipeListResponse(List.of());
                }
            }

        }
        // 재시도 루프 종료 직후
        if (res == null || res.getBody() == null) {
            return new RecipeListResponse(List.of());
        }

        //

        String text = extractText(res.getBody());   // candidates[0].content.parts[0].text
        String json = stripCodeFences(text);        // 혹시 섞여온 ``` 제거

        try {
            RecipeListResponse out = objectMapper.readValue(json, RecipeListResponse.class);
            if (out.getRecipes() == null) out.setRecipes(List.of());
            return out;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RecipeListResponse(List.of());
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> body) {
        if (body == null) return "";
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "";
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        if (content == null) return "";
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) return "";
        Object text = parts.get(0).get("text");
        return text == null ? "" : text.toString();
    }

    private String stripCodeFences(String s) {
        if (s == null) return "";
        return s.replaceAll("(?s)```json\\s*", "")
                .replaceAll("(?s)```\\s*", "")
                .trim();
    }
}
