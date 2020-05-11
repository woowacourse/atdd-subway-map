package wooteco.subway.admin.exception;

public class NotFoundStationException extends RuntimeException {
	public NotFoundStationException() {
		super("존재하지 않는 역입니다.");
	}
}
