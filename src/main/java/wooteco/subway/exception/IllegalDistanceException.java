package wooteco.subway.exception;

public class IllegalDistanceException extends IllegalInputException {

    public IllegalDistanceException() {
        super("[ERROR] 부적절한 거리 값입니다.");
    }
}
