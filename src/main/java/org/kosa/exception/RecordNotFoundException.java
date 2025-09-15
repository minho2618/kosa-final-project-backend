package org.kosa.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RecordNotFoundException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;
    private String title;

    public RecordNotFoundException(String message, String title, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.title = title;
    }

    public RecordNotFoundException(String message, String title) {
        this(message,title,HttpStatus.NOT_FOUND);
    }
}
