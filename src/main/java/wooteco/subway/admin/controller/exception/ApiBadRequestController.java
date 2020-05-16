package wooteco.subway.admin.controller.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import wooteco.subway.admin.exception.DuplicateLineNameException;
import wooteco.subway.admin.exception.DuplicateLineStationException;
import wooteco.subway.admin.exception.DuplicateStationNameException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@RestControllerAdvice
public class ApiBadRequestController {
	private static final String ILLEGAL_METHOD_ARGUMENT_EXCEPTION_MESSAGE = "요청시 같이 보낸 인자중 유효하지 않는값이 들어있습니다.";
	private static final String EMPTY_REQUEST_BODY_EXCEPTION_MESSAGE = "요청시 필요한 본문이 비어있습니다.";

	@ExceptionHandler(value = RuntimeException.class)
	public Map<String, String> getException(RuntimeException e) {
		return makeErrorMessage(e.getMessage());
	}

	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	public Map<String, String> getEmptyRequestBodyExceptionResponse(HttpMessageNotReadableException e) {
		return makeErrorMessage(EMPTY_REQUEST_BODY_EXCEPTION_MESSAGE);
	}

	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public Map<String, String> getRequestParameterExceptionResponse(MethodArgumentTypeMismatchException e) {
		return makeErrorMessage(ILLEGAL_METHOD_ARGUMENT_EXCEPTION_MESSAGE);
	}

	@ExceptionHandler(value = DuplicateLineNameException.class)
	public Map<String, String> getDuplicateLineException(DuplicateLineNameException e) {
		return makeErrorMessage(e.getMessage());
	}

	@ExceptionHandler(value = DuplicateStationNameException.class)
	public Map<String, String> getDuplicateStationException(DuplicateStationNameException e) {
		return makeErrorMessage(e.getMessage());
	}

	@ExceptionHandler(value = DuplicateLineStationException.class)
	public Map<String, String> getDuplicateLineStationException(DuplicateLineStationException e) {
		return makeErrorMessage(e.getMessage());
	}

	private Map<String, String> makeErrorMessage(String message) {
		Map<String, String> errorAttributes = new HashMap<>();
		errorAttributes.put("message", message);
		return errorAttributes;
	}
}
