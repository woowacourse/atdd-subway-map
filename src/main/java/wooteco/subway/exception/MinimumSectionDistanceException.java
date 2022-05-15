package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class MinimumSectionDistanceException extends SubwayException {

    public MinimumSectionDistanceException() {
        super(HttpStatus.BAD_REQUEST, "[ERROR] 구간 사이의 길이는 최소 1 이상이어야 합니다.");
    }
}
