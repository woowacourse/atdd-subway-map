package wooteco.subway.line.exception;

public enum ErrorCode {
    ALREADY_EXIST_LINE_NAME(400, "존재하는 노선 이름입니다."),
    NOT_EXIST_LINE_ID(400, "존재하는 노선 id 입니다.");

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
