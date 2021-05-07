package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionInformation {
    DUPLICATE_STATION_NAME_WHEN_INSERT(HttpStatus.CONFLICT, "이미 존재하는 역 이름은 추가할 수 없습니다."),
    STATION_NOT_FOUND_WHEN_DELETE(HttpStatus.BAD_REQUEST, "역 이름이 존재하지 않아 삭제할 수 없습니다."),
    DUPLICATE_LINE_NAME_WHEN_INSERT(HttpStatus.CONFLICT, "이미 존재하는 노선 이름은 추가할 수 없습니다."),
    LINE_NOT_FOUND_WHEN_LOOKUP(HttpStatus.NOT_FOUND, "노선 이름이 존재하지 않아 조회할 수 없습니다."),
    LINE_NOT_FOUND_WHEN_MODIFY(HttpStatus.NOT_FOUND, "노선 이름이 존재하지 않아 수정할 수 없습니다."),
    DUPLICATE_LINE_NAME_WHEN_MODIFY(HttpStatus.NOT_FOUND, "이미 존재하는 노선 이름으로 수정할 수 없습니다."),
    LINE_NOT_FOUND_WHEN_DELETE(HttpStatus.BAD_REQUEST, "노선 이름이 존재하지 않아 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionInformation(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage() {
        return this.message;
    }
}
