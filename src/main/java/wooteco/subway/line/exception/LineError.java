package wooteco.subway.line.exception;

public enum LineError {
    ALREADY_EXIST_LINE_NAME(400, "존재하는 노선 이름입니다."),
    NOT_EXIST_LINE_ID(400, "존재하는 노선 id 입니다."),
    INCORRECT_SIZE_LINE_FIND_BY_ID(500, "해당 ID에 해당하는 노선이 너무 많습니다."),
    NOT_EXIST_STATION_ON_LINE_REQUEST(400, "입력한 역은 존재하지 않습니다.");

    private final int statusCode;
    private final String message;

    LineError(int statusCode, String message) {
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
