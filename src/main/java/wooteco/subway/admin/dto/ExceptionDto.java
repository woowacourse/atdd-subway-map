package wooteco.subway.admin.dto;

public class ExceptionDto {
	private String message;

	public ExceptionDto() {
	}

	public ExceptionDto(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
