package wooteco.subway.admin.exception;

public class DuplicateLineNameException extends RuntimeException {
	private static final String EXIST_LINE_NAME_EXCEPTION_MESSAGE = "이미 사용중인 노선 이름입니다.";

	public DuplicateLineNameException() {
		super(EXIST_LINE_NAME_EXCEPTION_MESSAGE);
	}
}
