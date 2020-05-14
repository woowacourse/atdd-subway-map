package wooteco.subway.admin.exception;

public class AlreadyExistNameException extends RuntimeException {
    public AlreadyExistNameException(final String name) {
        super(name + "인 이름이 이미 존재합니다!");
    }
}
