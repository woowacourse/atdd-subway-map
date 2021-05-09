package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Void> unpredictableException(Exception error) {
//        System.err.println("@@@@");
//        System.err.println(error.getMessage());
//        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(error.getMessage());
    }
}
