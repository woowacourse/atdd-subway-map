package wooteco.subway.admin.exception;

public class DuplicatedValueException extends RuntimeException {
	public DuplicatedValueException(String message) {
		super(message + " : 값은 중복되는 값입니다.");
	}
}
