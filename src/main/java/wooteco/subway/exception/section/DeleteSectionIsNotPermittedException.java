package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class DeleteSectionIsNotPermittedException extends SubwayException {

    public DeleteSectionIsNotPermittedException() {
        super(HttpStatus.BAD_REQUEST, "구간을 삭제할 수 없습니다.");
    }
}
