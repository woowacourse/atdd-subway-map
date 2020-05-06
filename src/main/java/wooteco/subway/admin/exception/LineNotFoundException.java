package wooteco.subway.admin.exception;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(Long id) {
        super(id + "을 찾을 수 없습니다.");
    }
}
