package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;

public class SectionNotExistException extends SectionException {
    public SectionNotExistException() {
        super(HttpStatus.BAD_REQUEST, "앞 또는 뒤에 연결된 구간이 없습니다.");
    }
}
