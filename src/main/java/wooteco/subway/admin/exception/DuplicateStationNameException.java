package wooteco.subway.admin.exception;

public class DuplicateStationNameException extends RuntimeException {
	private static final String EXIST_STATION_NAME_EXCEPTION_MESSAGE = "이미 존재하는 역 이름입니다.";

	public DuplicateStationNameException() {
		super(EXIST_STATION_NAME_EXCEPTION_MESSAGE);
	}
}
