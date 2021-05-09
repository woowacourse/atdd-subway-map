package wooteco.subway.exception.line;

public class LineNameDuplicatedException extends RuntimeException {

    private static final String MESSAGE = "이미 등록되어 있는 노선 이름입니다.";

    public LineNameDuplicatedException() {
        super(MESSAGE);
    }
}
