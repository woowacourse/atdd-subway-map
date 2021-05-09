package wooteco.subway.exception;

public class LineNotFoundException extends RuntimeException {

    private static final String MESSAGE = "등록되어 있는 노선이 없습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }

}
