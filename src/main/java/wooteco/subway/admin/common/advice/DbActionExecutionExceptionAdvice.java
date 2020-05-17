package wooteco.subway.admin.common.advice;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DbActionExecutionExceptionAdvice {

	@ExceptionHandler(DbActionExecutionException.class)
	public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException e) {
		return ResponseEntity
			.badRequest()
			.body("중복된 데이터가 존재합니다.");
	}

}
