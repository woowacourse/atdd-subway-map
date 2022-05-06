package wooteco.subway.exception;

public class StationNameException extends IllegalArgumentException {

    public StationNameException() {
        super("[ERROR] 부적절한 역 이름입니다.");
    }
}
