package wooteco.subway.admin.exception;

public class DuplicateLineStationException extends RuntimeException {
    public DuplicateLineStationException() {
        super("노선 내에 중복된 역이 존재합니다.");
    }
}
