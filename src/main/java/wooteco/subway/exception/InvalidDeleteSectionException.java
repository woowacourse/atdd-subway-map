package wooteco.subway.exception;

public class InvalidDeleteSectionException extends SubwayException {
    public InvalidDeleteSectionException() {
        super("삭제할 수 없는 구간입니다.");
    }
}
