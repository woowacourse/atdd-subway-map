package wooteco.subway.admin.exception;

public class NotExistDataException extends RuntimeException {

	public NotExistDataException() {
	}

	public NotExistDataException(String message) {
		super(message);
	}
}
