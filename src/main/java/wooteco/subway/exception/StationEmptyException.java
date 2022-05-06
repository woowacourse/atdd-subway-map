package wooteco.subway.exception;

public class StationEmptyException extends EmptyArgumentException {

    public StationEmptyException() {
        super("지하철역 이름은 공백일 수 없습니다.");
    }
}
