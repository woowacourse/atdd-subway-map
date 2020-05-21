package wooteco.subway.admin.controller.exception;

public class NoLineExistException extends IllegalArgumentException {
	public NoLineExistException() {
		super("해당 id의 line이 없습니다.");
	}
}
