package wooteco.subway.exception.duplicate;

public class DuplicateStationException extends DuplicateException {

    public DuplicateStationException() {
        super("역은 중복될 수 없습니다.");
    }
}
