package wooteco.subway.exception;

public class LineNameException extends IllegalArgumentException {

    public LineNameException() {
        super("[ERROR] 부적절한 노선 이름입니다.");
    }
}
