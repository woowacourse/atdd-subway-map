package wooteco.subway.admin.exception;

public class NotFoundLineException extends RuntimeException {
	private static final String NOT_EXIST_LINE_EXCEPTION_MESSAGE = "존재하지 않는 노선 입니다.";

	public NotFoundLineException() {
		super(NOT_EXIST_LINE_EXCEPTION_MESSAGE);
	}
}
