package wooteco.subway.line.exception;

public enum SectionError {
    SAME_STATION_INPUT(400, "하행과 상행이 같은 역일 수 없습니다."),
    BOTH_STATION_IN_PATH(400, "구간의 역이 둘다 노선에 포함되어 있습니다."),
    NONE_STATION_IN_PATH(400, "구간의 역이 둘다 노선에 포함되어 있지 않습니다.");

    private final int statusCode;
    private final String message;

    SectionError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
