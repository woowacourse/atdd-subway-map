package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public enum SubwayException {

    DUPLICATE_STATION_EXCEPTION("중복된 역입니다.", HttpStatus.BAD_REQUEST.value()),
    DUPLICATE_LINE_EXCEPTION("중복된 노선입니다.", HttpStatus.BAD_REQUEST.value()),
    DUPLICATE_SECTION_EXCEPTION("중복된 구간입니다.", HttpStatus.BAD_REQUEST.value()),
    ILLEGAL_SECTION_IN_NOT_EXIST_STATION_EXCEPTION("노선에 존재하지 않는 역입니다.",
        HttpStatus.BAD_REQUEST.value()),
    ILLEGAL_SECTION_EXCEPTION("구간을 추가할 위치를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    ILLEGAL_SECTION_DISTANCE_EXCEPTION("나눌 수 없는 거리입니다.", HttpStatus.BAD_REQUEST.value()),
    ILLEGAL_SECTION_DELETE_EXCEPTION("구간이 1개인 경우 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    ILLEGAL_STATION_DELETE_EXCEPTION("사용중인 지하철역은 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    INVALID_INPUT_NAME_OR_COLOR_EXCEPTION("이름과 색상은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    INVALID_INPUT_STATION_ID_EXCEPTION("구간추가시 역은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    INVALID_INPUT_DISTANCE_EXCEPTION("구간의 거리는 0 이하일 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    NOT_EXIST_LINE_EXCEPTION("존재하지 않는 노선입니다.", HttpStatus.BAD_REQUEST.value()),
    NOT_EXIST_STATION_EXCEPTION("존재하지 않는 역입니다.", HttpStatus.BAD_REQUEST.value());

    private final String message;
    private final int status;

    SubwayException(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String message() {
        return message;
    }

    public int status() {
        return status;
    }
}
