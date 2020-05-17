package wooteco.subway.admin.controller.advice;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.controller.DefinedSqlException;
import wooteco.subway.admin.domain.line.relation.InvalidLineStationException;
import wooteco.subway.admin.domain.line.vo.InvalidLineTimeTableException;
import wooteco.subway.admin.dto.SubwayErrorMessage;

@RestControllerAdvice
public class ControllerAdvice {
	@ExceptionHandler({InvalidLineTimeTableException.class, InvalidLineStationException.class,
		IllegalArgumentException.class})
	public ResponseEntity<SubwayErrorMessage> getException(Exception e) {
		return ResponseEntity.badRequest().body(new SubwayErrorMessage(e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<SubwayErrorMessage> getException(MethodArgumentNotValidException e) {
		FieldError error = e.getBindingResult().getFieldError();
		String defaultMessage = Objects.requireNonNull(error).getDefaultMessage();
		return ResponseEntity.badRequest().body(new SubwayErrorMessage(defaultMessage));
	}

	@ExceptionHandler(DefinedSqlException.class)
	public ResponseEntity<SubwayErrorMessage> getDefinedSQLException(DefinedSqlException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new SubwayErrorMessage(e.getMessage()));
	}
}
