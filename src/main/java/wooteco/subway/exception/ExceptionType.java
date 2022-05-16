package wooteco.subway.exception;

public enum ExceptionType {

    STATION_NOT_FOUND("존재하지 않는 역을 입력하였습니다."),
    LINE_NOT_FOUND("존재하지 않는 노선입니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
