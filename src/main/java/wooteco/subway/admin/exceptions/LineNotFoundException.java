package wooteco.subway.admin.exceptions;

public class LineNotFoundException extends IllegalArgumentException {
    private static final String message = "id=%d 노선이 존재하지 않습니다.";

    public LineNotFoundException(Long id) {
        super(String.format(message, id));
    }
}
