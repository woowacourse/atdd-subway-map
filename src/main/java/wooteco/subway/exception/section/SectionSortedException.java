package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class SectionSortedException extends SubwayException {
    public SectionSortedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "구간 정보가 올바르지 않습니다.");
    }
}
