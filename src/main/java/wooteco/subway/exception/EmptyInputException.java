package wooteco.subway.exception;

public class EmptyInputException extends SubwayException {

    public EmptyInputException() {
        super("빈 값이 입력되었습니다.");
    }
}
