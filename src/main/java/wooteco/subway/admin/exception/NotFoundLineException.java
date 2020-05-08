package wooteco.subway.admin.exception;

public class NotFoundLineException extends RuntimeException {
	private static final String NOT_FOUND_LINE_EXCEPTION_MESSAGE = "존재하지 않는 노선";

	public NotFoundLineException() {
		super(NOT_FOUND_LINE_EXCEPTION_MESSAGE);
	}
}
