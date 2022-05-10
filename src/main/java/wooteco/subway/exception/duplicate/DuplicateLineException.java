package wooteco.subway.exception.duplicate;

public class DuplicateLineException extends DuplicateException {

    public DuplicateLineException() {
        super("노선은 중복될 수 없습니다.");
    }
}
