package wooteco.subway.admin.controller.exception;

public class NoStationExistException extends IllegalArgumentException {
	public NoStationExistException(String message) {
		super(message);
	}
}
