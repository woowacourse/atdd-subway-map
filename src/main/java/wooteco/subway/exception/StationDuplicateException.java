package wooteco.subway.exception;

public class StationDuplicateException extends DuplicateException {

    public StationDuplicateException() {
        super("두 종점은 같을 수 없습니다.");
    }
}
