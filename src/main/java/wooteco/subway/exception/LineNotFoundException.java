package wooteco.subway.exception;

public class LineNotFoundException extends NotFoundException {

    public LineNotFoundException() {
        super("존재하지 않는 지하철 노선입니다.");
    }
}
