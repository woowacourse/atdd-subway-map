package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class SectionException extends SubwayException {
    public SectionException(HttpStatus httpStatus, Object body) {
        super(httpStatus, body);
    }
}
