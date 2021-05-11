package wooteco.subway.exception;

public class StationDuplicationException extends SubwayException {
    public StationDuplicationException() {
        super("이미 존재하는 역입니다.");
    }
}
