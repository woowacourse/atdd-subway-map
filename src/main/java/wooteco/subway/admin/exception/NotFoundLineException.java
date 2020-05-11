package wooteco.subway.admin.exception;

public class NotFoundLineException extends RuntimeException {
	public NotFoundLineException() {
		super("존재하지 않는 노선 입니다.");
	}
}
