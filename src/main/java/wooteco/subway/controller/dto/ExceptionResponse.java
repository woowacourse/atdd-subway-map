package wooteco.subway.controller.dto;

public class ExceptionResponse {
	private String message;

	public ExceptionResponse() {
	}

	public ExceptionResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
