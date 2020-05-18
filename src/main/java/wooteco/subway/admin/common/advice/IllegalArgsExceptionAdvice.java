package wooteco.subway.admin.common.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.common.response.DefaultResponse;

@RestControllerAdvice
public class IllegalArgsExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(IllegalArgumentException.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DefaultResponse<Void>> handleIllegalArgsException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(DefaultResponse.error("잘못된 요청입니다."));
    }

}
