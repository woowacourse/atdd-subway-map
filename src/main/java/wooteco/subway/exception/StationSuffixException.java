package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class StationSuffixException extends SubwayException {
    public StationSuffixException() {
        super(HttpStatus.BAD_REQUEST, "-역으로 끝나는 이름을 입력해주세요.");
    }
}
