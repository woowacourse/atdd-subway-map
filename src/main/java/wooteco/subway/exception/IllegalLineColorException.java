package wooteco.subway.exception;

public class IllegalLineColorException extends IllegalInputException {

    public IllegalLineColorException() {
        super("[ERROR] 부적절한 노선 색깔입니다.");
    }
}
