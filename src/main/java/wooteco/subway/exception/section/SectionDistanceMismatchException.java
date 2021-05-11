package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class SectionDistanceMismatchException extends SubwayException {
    public SectionDistanceMismatchException() {
        super(HttpStatus.BAD_REQUEST, "삽입하려는 구간의 길이가 기존에 포함된 구간의 길이보다 크거나 같습니다.");
    }
}
