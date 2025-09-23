package org.kosa.advice;

import org.kosa.exception.DuplicateException;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class DefaultExceptionAdvice {
    @ExceptionHandler(RecordNotFoundException.class)
    public ProblemDetail recordNotFoundExceptionHandle(RecordNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus().value());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());

        //추가적으로 정보를 설정할 부분은 아랫코드로 작성..
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(DuplicateException.class)
    public ProblemDetail duplicateExceptionHandle(DuplicateException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus().value());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());

        //추가적으로 정보를 설정할 부분은 아랫코드로 작성..
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail invalidInputExceptionHandle(InvalidInputException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus().value());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());

        //추가적으로 정보를 설정할 부분은 아랫코드로 작성..
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(
            MaxUploadSizeExceededException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "파일 크기가 제한을 초과했습니다");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail exceptionHandle(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(600);//상태코드중 하나 넣어도 상관없음.
        problemDetail.setTitle("DB Error...");
        problemDetail.setDetail("작업중 문제가 발생하였습니다.");

        //추가적으로 정보를 설정할 부분은 아랫코드로 작성..
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}
