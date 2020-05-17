package wooteco.subway.admin.exception;

public class DuplicatedStationException extends RuntimeException {
    public DuplicatedStationException(String name) {
        super("이미 추가된 역: " + name);
    }
}
