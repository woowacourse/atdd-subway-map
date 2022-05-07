package wooteco.subway.ui;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.exception.StationDuplicateException;
import wooteco.subway.ui.dto.ExceptionResponse;

@RestControllerAdvice(basePackageClasses = {LineController.class, StationController.class})
public class SubwayControllerAdvice {

    private static final String UNKNOWN_EXCEPTION_MESSAGE = "확인되지 않은 예외가 발생했습니다. 관리자에게 문의해주세요.";

    @ExceptionHandler({StationDuplicateException.class, LineDuplicateException.class, NotFoundLineException.class,
            NotFoundStationException.class, DuplicateKeyException.class})
    public ResponseEntity<ExceptionResponse> duplicateStation(Exception e) {
        return new ResponseEntity<>(ExceptionResponse.from(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> unknownException(Exception e) {
        return new ResponseEntity<>(ExceptionResponse.from(UNKNOWN_EXCEPTION_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
