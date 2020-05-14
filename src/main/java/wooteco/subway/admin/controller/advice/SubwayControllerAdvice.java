package wooteco.subway.admin.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;

@RestControllerAdvice
public class SubwayControllerAdvice {
	private static final Logger logger = LogManager.getLogger("SubwayControllerAdvice");

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> getIllegalArgumentException(IllegalArgumentException e) {
		logger.error(e.getMessage());
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DbActionExecutionException.class)
	public ResponseEntity<String> getDbActionExecutionException(DbActionExecutionException e) {
		logger.error(e.getMessage());
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> getHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		logger.error(e.getMessage());
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnexpectedTypeException.class)
	public ResponseEntity<String> getUnexpectedTypeException(UnexpectedTypeException e) {
		logger.error(e.getMessage());
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
