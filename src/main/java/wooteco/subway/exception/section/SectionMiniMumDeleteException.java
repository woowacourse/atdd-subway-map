package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;

public class SectionMiniMumDeleteException extends SectionException {
    public SectionMiniMumDeleteException(Long lineId) {
        super(HttpStatus.BAD_REQUEST, lineId + " 노선의 구간이 1개라 삭제할 수 없습니다.");
    }
}
