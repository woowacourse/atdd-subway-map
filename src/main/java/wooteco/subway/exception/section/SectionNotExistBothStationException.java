package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Section;

public class SectionNotExistBothStationException extends SectionException {
    public SectionNotExistBothStationException(Section section) {
        super(HttpStatus.BAD_REQUEST,
                "상행 : " + section.getUpStationId()
                        + ", 하행 : " + section.getDownStationId()
                        + " 역들은 노선 : " + section.getLineId() + "에 존재하지 않아서 추가할 수 없습니다.");
    }
}
