package wooteco.subway.exception.handler;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.constant.BlankArgumentException;
import wooteco.subway.exception.constant.DuplicateException;
import wooteco.subway.exception.constant.NotExistException;
import wooteco.subway.exception.constant.SectionNotDeleteException;
import wooteco.subway.exception.dto.ErrorResult;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotExistException.class)
    private ErrorResult handleExceptionToNotFound(Exception e) {
        return new ErrorResult(NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            DuplicateException.class,
            BlankArgumentException.class,
            DuplicateKeyException.class,
            SectionNotDeleteException.class})
    private ErrorResult handleExceptionToBadRequest(Exception e) {
        return new ErrorResult(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    private ErrorResult handleExceptionToInternalServerError() {
        return new ErrorResult(INTERNAL_SERVER_ERROR);
    }
}
