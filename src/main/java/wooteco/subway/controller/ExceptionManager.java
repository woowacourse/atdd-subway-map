package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionManager {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> handle() {
		return ResponseEntity.badRequest().build();
	}
}
