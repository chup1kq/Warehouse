package db.warehouse.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseAppException extends RuntimeException{
    private final String code;
    private final HttpStatus httpStatus;

    public BaseAppException(ExceptionName exceptionName) {
        super(exceptionName.getMessage());
        this.code = exceptionName.getCode();
        this.httpStatus = exceptionName.getHttpStatus();
    }
}
