package wooteco.subway.station.exception;

public enum ErrorCode {
    ALREADY_EXIST_STATION_NAME(400, "존재하는 역 이름입니다.");

    private int statusCode;
    private String message;

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
