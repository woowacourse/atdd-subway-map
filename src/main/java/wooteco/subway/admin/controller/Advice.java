package wooteco.subway.admin.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Advice {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "잘못된 값을 입력했습니다.")
	public void handler(Exception e) {
	}

	@ExceptionHandler(DbActionExecutionException.class)
	public ResponseEntity dbActionException(Exception e) {
		if (e.getCause() instanceof DuplicateKeyException) {
			return ResponseEntity
				.status(400)
				.body("중복된 이름입니다.");
		}
		return
			ResponseEntity
				.status(400)
				.build();
	}
}
