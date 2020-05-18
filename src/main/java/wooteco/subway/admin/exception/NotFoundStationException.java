package wooteco.subway.admin.exception;

public class NotFoundStationException extends RuntimeException {
	private static final String NOT_EXIST_STATION_EXCEPTION_MESSAGE = "존재하지 않는 역입니다.";

	public NotFoundStationException() {
		super(NOT_EXIST_STATION_EXCEPTION_MESSAGE);
	}
}
