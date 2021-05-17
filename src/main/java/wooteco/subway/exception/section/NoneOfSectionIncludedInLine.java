package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class NoneOfSectionIncludedInLine extends SubwayException {
    public NoneOfSectionIncludedInLine() {
        super(HttpStatus.BAD_REQUEST, "상행, 하행 중 하나라도 포함된 구간이 존재하지 않습니다.");
    }
}
