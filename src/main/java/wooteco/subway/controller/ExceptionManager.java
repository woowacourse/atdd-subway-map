package wooteco.subway.controller;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> handleIllegalInput() {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Void> handleNoData() {
		return ResponseEntity.notFound().build();
	}
}
