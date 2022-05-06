package wooteco.subway.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.controller.dto.ExceptionResponse;

@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionResponse> handleIllegalInput() {
		return ResponseEntity.badRequest()
			.body(new ExceptionResponse("입력이 잘못되었습니다."));
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ExceptionResponse> handleNoData() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ExceptionResponse("리소스를 찾을 수 없습니다."));
	}
}
