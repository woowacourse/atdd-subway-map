package wooteco.subway.exception;

public class LineEmptyException extends EmptyArgumentException {

    public LineEmptyException() {
        super("지하철 노선 이름은 공백일 수 없습니다.");
    }
}
