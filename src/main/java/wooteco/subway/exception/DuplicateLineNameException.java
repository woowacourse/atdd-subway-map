package wooteco.subway.exception;

public class DuplicateLineNameException extends IllegalArgumentException {

    public DuplicateLineNameException() {
        super("같은 이름을 가진 노선이 이미 있습니다.");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
