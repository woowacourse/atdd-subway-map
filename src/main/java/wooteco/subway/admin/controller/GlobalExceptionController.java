package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionController {
	@ExceptionHandler(value = RuntimeException.class)
	public ResponseEntity<String> getException(RuntimeException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
