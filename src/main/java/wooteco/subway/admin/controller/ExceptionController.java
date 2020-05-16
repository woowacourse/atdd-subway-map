package wooteco.subway.admin.controller;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.ExceptionResponse;

@RestController
@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionResponse> getIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest()
			.body(new ExceptionResponse(e.getMessage()));
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ExceptionResponse> getNoSuchElementException(NoSuchElementException e){
		return ResponseEntity.badRequest()
			.body(new ExceptionResponse(e.getMessage()));
	}
}
