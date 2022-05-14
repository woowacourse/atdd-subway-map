package wooteco.subway.ui;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.SubwayException;
import wooteco.subway.dto.ExceptionResponse;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicatedName(SubwayException subwayException) {
        return ResponseEntity.unprocessableEntity()
                .body(new ExceptionResponse(subwayException.getMessage()));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ExceptionResponse> handleNoExistId() {
        return ResponseEntity.unprocessableEntity()
                .body(new ExceptionResponse("존재하지 않는 id입니다."));
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ExceptionResponse> handleBadRequest() {
//        return ResponseEntity.badRequest()
//                .body(new ExceptionResponse("올바르지 않은 요청입니다."));
//    }
}
