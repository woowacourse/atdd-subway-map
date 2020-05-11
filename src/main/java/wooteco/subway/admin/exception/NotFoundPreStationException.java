package wooteco.subway.admin.exception;

public class NotFoundPreStationException extends RuntimeException {
	public NotFoundPreStationException() {
		super("이전역으로 연결할 역이 노선구간 상에 존재하지 않습니다.");
	}
}
