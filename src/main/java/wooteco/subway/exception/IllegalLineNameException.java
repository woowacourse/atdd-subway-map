package wooteco.subway.exception;

public class IllegalLineNameException extends IllegalInputException {

    public IllegalLineNameException() {
        super("[ERROR] 부적절한 노선 이름입니다.");
    }
}
