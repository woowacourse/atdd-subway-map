package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Section;

public class SectionDistanceException extends SectionException {
    public SectionDistanceException(Section newSection, Section otherSection) {
        super(HttpStatus.BAD_REQUEST, "기존 구간 길이인 " + otherSection.getDistance() + "보다 "
                + newSection.getDistance() + "가 같거나 커서 구간을 추가할 수 없습니다.");
    }
}
