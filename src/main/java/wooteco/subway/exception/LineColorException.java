package wooteco.subway.exception;

public class LineColorException extends IllegalArgumentException {

    public LineColorException() {
        super("[ERROR] 부적절한 노선 색깔입니다.");
    }
}
