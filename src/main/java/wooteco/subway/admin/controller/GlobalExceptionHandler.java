package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import wooteco.subway.admin.dto.ErrorResponse;
import wooteco.subway.admin.exception.DuplicateLineNameException;
import wooteco.subway.admin.exception.DuplicateLineStationException;
import wooteco.subway.admin.exception.DuplicateStationNameException;
import wooteco.subway.admin.exception.NotFoundLineException;
import wooteco.subway.admin.exception.NotFoundPreStationException;
import wooteco.subway.admin.exception.NotFoundStationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버 내부에 오류가 있습니다.";
	private static final String EMPTY_REQUEST_BODY_EXCEPTION_MESSAGE = "요청시 필요한 본문이 비어있습니다.";
	private static final String ILLEGAL_METHOD_ARGUMENT_EXCEPTION_MESSAGE = "요청시 같이 보낸 인자중 유효하지 않는값이 들어있습니다.";

	@ExceptionHandler(value = RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleInternalException() {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse(INTERNAL_SERVER_ERROR_MESSAGE));
	}

	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleEmptyBodyException() {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(EMPTY_REQUEST_BODY_EXCEPTION_MESSAGE));
	}

	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleNonEffectiveParameterException() {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(ILLEGAL_METHOD_ARGUMENT_EXCEPTION_MESSAGE));
	}

	@ExceptionHandler(value = {DuplicateLineNameException.class, DuplicateStationNameException.class,
		DuplicateLineStationException.class,})
	public ResponseEntity<ErrorResponse> handleDuplicateCreateException(RuntimeException e) {
		return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
	}

	@ExceptionHandler(value = {NotFoundStationException.class, NotFoundPreStationException.class,
		NotFoundLineException.class})
	public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse(e.getMessage()));
	}
}
