package org.kosa.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kosa.exception.DuplicateException;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionAdvice {

    @ExceptionHandler(RecordNotFoundException.class)
    public ProblemDetail handleNotFound(RecordNotFoundException e, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(e.getHttpStatus());
        pd.setTitle(e.getTitle());
        pd.setDetail(e.getMessage());
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @ExceptionHandler(DuplicateException.class)
    public ProblemDetail handleDuplicate(DuplicateException e, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(e.getHttpStatus()); // 보통 409 추천
        pd.setTitle(e.getTitle());
        pd.setDetail(e.getMessage());
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalid(InvalidInputException e, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(e.getHttpStatus()); // 보통 400/422 추천
        pd.setTitle(e.getTitle());
        pd.setDetail(e.getMessage());
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    // 업스트림(Gemini)에서 4xx/5xx 응답을 보낸 경우(네트워크 성공 + HTTP 에러)
    @ExceptionHandler(HttpStatusCodeException.class)
    public ProblemDetail handleUpstream(HttpStatusCodeException e, HttpServletRequest req) {
        log.error("Upstream error from Gemini: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY); // 502
        pd.setTitle("Upstream Error");
        pd.setDetail("Gemini API 에러: " + e.getStatusCode());
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    // 업스트림 네트워크 자체가 실패(타임아웃/연결거부 등)
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleNetwork(ResourceAccessException e, HttpServletRequest req) {
        log.error("Network error to Gemini", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY); // 502 (타임아웃이면 504도 고려)
        pd.setTitle("Network Error");
        pd.setDetail("Gemini API 네트워크 오류");
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    // 마지막 안전망 — 절대 600 쓰지 말고 500!
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAny(Exception e, HttpServletRequest req) {
        log.error("Unhandled exception", e);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR); // ★ 500
        pd.setTitle("Server Error");
        pd.setDetail("작업 중 문제가 발생했습니다.");
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }
}
