package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.ExceptionResponse;

@RestController
@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionResponse> getIllegalArgumentException(IllegalArgumentException e){
		return ResponseEntity.badRequest()
			.body(new ExceptionResponse(e.getMessage()));
	}
}
