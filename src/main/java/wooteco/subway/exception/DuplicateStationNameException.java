package wooteco.subway.exception;

public class DuplicateStationNameException extends RuntimeException {
    public DuplicateStationNameException() {
        super("중복된 역 이름이 있습니다.");
    }
}
