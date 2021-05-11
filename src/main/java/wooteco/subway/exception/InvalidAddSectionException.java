package wooteco.subway.exception;

public class InvalidAddSectionException extends SubwayException {
    public InvalidAddSectionException() {
        super("추가할 수 없는 구간입니다.");
    }
}
