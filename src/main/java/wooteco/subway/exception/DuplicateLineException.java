package wooteco.subway.exception;

public class DuplicateLineException extends IllegalArgumentException {

    public DuplicateLineException() {
        super("같은 이름 혹은 색깔을 가진 노선이 이미 있습니다.");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
