package wooteco.subway.admin.exception;

public class DuplicateLineStationException extends RuntimeException {
	public DuplicateLineStationException() {
		super("이미 해당 역은 이미 해당 노선구간에 포함되어 있습니다.");
	}
}
