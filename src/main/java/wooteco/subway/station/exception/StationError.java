package wooteco.subway.station.exception;

public enum StationError {
    ALREADY_EXIST_STATION_NAME(400, "존재하는 역 이름입니다."),
    NO_STATION_BY_ID(400, "해당 ID에 역은 존재하지 않습니다");

    private final int statusCode;
    private final String message;

    StationError(int statusCode, String message) {
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
