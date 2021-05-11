package wooteco.subway.exception;

public class DuplicateStationException extends RuntimeException {

    public DuplicateStationException() {
        super("중복된 역이 존재합니다.");
    }
}
