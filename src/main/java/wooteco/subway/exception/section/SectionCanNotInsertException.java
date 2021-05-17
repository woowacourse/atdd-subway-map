package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class SectionCanNotInsertException extends SubwayException {
    public SectionCanNotInsertException() {
        super(HttpStatus.BAD_REQUEST, "구간을 추가할 수 없습니다.");
    }
}
