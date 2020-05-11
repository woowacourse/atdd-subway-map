package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Advice {
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handler(Exception e) {
		return ResponseEntity
			.status(400)
			.body(e.getMessage());
	}
}
