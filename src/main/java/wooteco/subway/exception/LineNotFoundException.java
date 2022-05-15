package wooteco.subway.exception;

public class LineNotFoundException extends NotFoundException {
    public LineNotFoundException() {
        super("해당 노선을 찾지 못했습니다.");
    }
}
