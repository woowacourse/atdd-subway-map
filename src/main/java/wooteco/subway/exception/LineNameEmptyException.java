package wooteco.subway.exception;

public class LineNameEmptyException extends EmptyArgumentException {

    public LineNameEmptyException() {
        super("지하철 노선 이름은 공백일 수 없습니다.");
    }
}
