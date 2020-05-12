package wooteco.subway.admin.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.ExistingNameException;
import wooteco.subway.admin.exception.LineStationException;
import wooteco.subway.admin.exception.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(ExistingNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError existingNameException(ExistingNameException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError notFoundException(NotFoundException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(LineStationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError lineStationException(LineStationException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
