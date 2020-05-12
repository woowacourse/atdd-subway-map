package wooteco.subway.admin.exceptions;

public class StationNotFoundException extends IllegalArgumentException {
    private static final String message = "id=%d 역이 존재하지 않습니다.";

    public StationNotFoundException(Long id) {
        super(String.format(message, id));
    }
}
