package wooteco.subway.admin.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.common.exception.NotSavedException;
import wooteco.subway.admin.common.exception.SubwayException;
import wooteco.subway.admin.common.response.DefaultResponse;

@RestControllerAdvice
public class SubWayExceptionAdvice {


    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<DefaultResponse<Void>> handleIllegalArgsException(SubwayException e) {
        return ResponseEntity.badRequest().body(DefaultResponse.error("잘못된 요청입니다."));
    }

    @ExceptionHandler(NotSavedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotSavedException(NotSavedException e) {
        return ResponseEntity.badRequest().body(DefaultResponse.error(e.getMessage()));
    }
}
