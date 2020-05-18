package wooteco.subway.admin.controller;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.InvalidLineStationException;
import wooteco.subway.admin.exception.InvalidNameException;
import wooteco.subway.admin.exception.NotFoundLineIdException;

@RestControllerAdvice
public class AdviceController {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundLineIdException.class)
    public String handleNotFoundLineIdException(NotFoundLineIdException e) {
        return e.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidNameException.class)
    public String handleInvalidNameException(InvalidNameException e) {
        return e.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidLineStationException.class)
    public String handleInvalidLineStationException(InvalidLineStationException e) {
        return e.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DbActionExecutionException.class)
    public String handleDuplicatedNameException() {
        return "중복된 이름이 존재합니다.";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        e.printStackTrace();
        return "알 수 없는 에러입니다.";
    }
}
