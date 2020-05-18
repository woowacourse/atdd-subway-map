package wooteco.subway.admin.exception;

public class NotFoundPreStationException extends RuntimeException {
	private static final String NOT_EXIST_LINE_STATION_IN_LINE_EXCEPTION_MESSAGE = "이전역으로 연결할 역이 노선구간 상에 존재하지 않습니다.";

	public NotFoundPreStationException() {
		super(NOT_EXIST_LINE_STATION_IN_LINE_EXCEPTION_MESSAGE);
	}
}
