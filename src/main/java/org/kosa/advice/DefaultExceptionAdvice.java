package org.kosa.advice;

import org.kosa.exception.DuplicateException;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

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
}
