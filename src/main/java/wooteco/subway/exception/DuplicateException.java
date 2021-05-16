package wooteco.subway.exception;

public class DuplicateException extends SubwayException {

    public DuplicateException() {
        super("중복된 정보가 존재합니다.");
    }
}
