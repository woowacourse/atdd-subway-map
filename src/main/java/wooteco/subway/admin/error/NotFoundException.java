package wooteco.subway.admin.error;

public class NotFoundException extends RuntimeException {
    public static final String STATION_NOT_FOUND = "해당하는 역(Station)을 찾을 수 없습니다.";
    public static final String LINE_NOT_FOUND = "해당하는 노선(Line)을 찾을 수 없습니다.";
    public static final String FIRST_STATION_NOT_FOUND = "첫번째 역(Station)을 찾을 수 없습니다";

    public NotFoundException(String message) {
        super(message);
    }
}
