package wooteco.subway.exception;

public class IllegalSectionException extends IllegalInputException {

    public IllegalSectionException() {
        super("[ERROR] 부적절한 구간입니다.");
    }
}
