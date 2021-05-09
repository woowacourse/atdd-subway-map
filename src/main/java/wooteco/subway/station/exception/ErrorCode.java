package wooteco.subway.station.exception;

public enum ErrorCode {
    ALREADY_EXIST_STATION_NAME(400, "존재하는 역 이름입니다."),
    INCORRECT_SIZE_STATION_FIND_BY_ID(500, "해당 ID에 해당하는 역이 많습니다.");

    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
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
