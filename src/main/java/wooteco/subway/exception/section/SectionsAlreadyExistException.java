package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class SectionsAlreadyExistException extends SubwayException {
    public SectionsAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST, "이미 해당 노선에 상행 종점과 하행 종점이 모두 포함되어있습니다.");
    }
}
