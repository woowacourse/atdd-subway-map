package wooteco.subway.exception;

public class SectionDuplicateException extends DuplicateException {

    public SectionDuplicateException() {
        super("중복된 구간입니다.");
    }
}
