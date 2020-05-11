package wooteco.subway.admin.exception;

public class DuplicateStationNameException extends RuntimeException {
	public DuplicateStationNameException() {
		super("이미 존재하는 역 이름입니다.");
	}
}
