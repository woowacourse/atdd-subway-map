package wooteco.subway.exception;

public class IllegalStationNameException extends IllegalInputException {

    public IllegalStationNameException() {
        super("[ERROR] 부적절한 역 이름입니다.");
    }
}
