package wooteco.subway.admin.exception;

public class DuplicateLineNameException extends RuntimeException {
	public DuplicateLineNameException() {
		super("이미 사용중인 노선 이름입니다.");
	}
}
