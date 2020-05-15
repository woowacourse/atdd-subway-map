package wooteco.subway.admin.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.dto.ExceptionDto;

@RestControllerAdvice
public class ExceptionAdvice {
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionDto> handleException(RuntimeException e) {
		return ResponseEntity.badRequest().body(new ExceptionDto(e.getMessage()));
	}
}
