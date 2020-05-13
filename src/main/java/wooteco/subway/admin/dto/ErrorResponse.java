package wooteco.subway.admin.dto;

/**
 *    에러를 전달하는 DTO 클래스입니다.
 *
 *    @author HyungJu An
 */
public class ErrorResponse {
	private String errorMessage;

	ErrorResponse() {
	}

	ErrorResponse(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static ErrorResponse of(final String errorMessage) {
		return new ErrorResponse(errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
