package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class BothEndStationsSameException extends SubwayException {
    public BothEndStationsSameException() {
        super(HttpStatus.BAD_REQUEST, "상행 종점과 하행 종점이 같을 수 없습니다.");
    }
}
