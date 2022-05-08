package wooteco.subway.exception;

public class DuplicateNameException extends RuntimeException {

    public DuplicateNameException() {
        super("중복된 이름으로는 생성할 수 없습니다.");
    }
}
