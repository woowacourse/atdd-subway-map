package wooteco.subway.admin.controller.exception;

public class NoLineExistException extends IllegalArgumentException {
	public NoLineExistException(String message) {
		super(message);
	}
}
