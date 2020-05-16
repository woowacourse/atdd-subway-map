package wooteco.subway.admin.controller.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.NotFoundLineException;
import wooteco.subway.admin.exception.NotFoundPreStationException;
import wooteco.subway.admin.exception.NotFoundStationException;

@ResponseStatus(HttpStatus.NOT_FOUND)
@RestControllerAdvice
public class ApiNotFoundController {
	@ExceptionHandler(value = NotFoundStationException.class)
	public Map<String, String> getNoStationException(NotFoundStationException e) {
		return makeErrorMessage(e.getMessage());
	}

	@ExceptionHandler(value = NotFoundPreStationException.class)
	public Map<String, String> getNoPreStationException(NotFoundPreStationException e) {
		return makeErrorMessage(e.getMessage());
	}

	@ExceptionHandler(value = NotFoundLineException.class)
	public Map<String, String> getNoLineException(NotFoundLineException e) {
		return makeErrorMessage(e.getMessage());
	}

	private Map<String, String> makeErrorMessage(String message) {
		Map<String, String> errorAttributes = new HashMap<>();
		errorAttributes.put("message", message);
		return errorAttributes;
	}
}
