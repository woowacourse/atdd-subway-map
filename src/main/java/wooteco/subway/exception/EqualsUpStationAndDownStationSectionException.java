package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class EqualsUpStationAndDownStationSectionException extends SubwayException {

    public EqualsUpStationAndDownStationSectionException() {
        super(HttpStatus.BAD_REQUEST, "[ERROR] 동일한 역으로 구간을 등록할 수 없습니다.");
    }
}
