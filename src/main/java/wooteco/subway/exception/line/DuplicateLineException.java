package wooteco.subway.exception.line;

public class DuplicateLineException extends RuntimeException {

    public DuplicateLineException() {
        super("이미 해당 노선의 속성은 존재합니다.");
    }
}
