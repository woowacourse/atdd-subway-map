package wooteco.subway.admin.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.common.exception.SubwayException;

@RestControllerAdvice
public class SubwayExceptionAdvice {

	@ExceptionHandler(SubwayException.class)
	public ResponseEntity<String> handleSubwayException(SubwayException e) {
		return ResponseEntity
			.badRequest()
			.body(e.getMessage());
	}

}
