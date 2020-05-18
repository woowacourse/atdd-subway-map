package wooteco.subway.admin.exception;

public class DuplicateLineStationException extends RuntimeException {
	private static final String EXIST_LINE_STATION_IN_LINE_EXCEPTION_MESSAGE = "이미 해당 역은 이미 해당 노선구간에 포함되어 있습니다.";

	public DuplicateLineStationException() {
		super(EXIST_LINE_STATION_IN_LINE_EXCEPTION_MESSAGE);
	}
}
