package wooteco.subway.exception.notFoundException;

public class LineNotFoundException extends NotFoundException {

    private static final String LINE_NOT_FOUND = "일치하는 노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(LINE_NOT_FOUND);
    }
}
