package wooteco.subway.exception;

public class DuplicateStationNameException extends IllegalArgumentException {

    public DuplicateStationNameException() {
        super("같은 이름을 가진 역이 이미 있습니다");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
