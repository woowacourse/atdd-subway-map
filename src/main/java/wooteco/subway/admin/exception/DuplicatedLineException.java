package wooteco.subway.admin.exception;

public class DuplicatedLineException extends RuntimeException {
	public DuplicatedLineException(String name) {
		super("이미 추가된 노선: " + name);
	}
}
