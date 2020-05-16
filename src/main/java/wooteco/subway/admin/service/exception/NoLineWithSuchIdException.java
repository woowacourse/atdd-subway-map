package wooteco.subway.admin.service.exception;

public class NoLineWithSuchIdException extends IllegalArgumentException {
	public NoLineWithSuchIdException() {
		super("해당 id를 가진 Line이 존재하지 않습니다.");
	}
}
