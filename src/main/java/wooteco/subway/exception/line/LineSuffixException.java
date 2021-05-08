package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class LineSuffixException extends SubwayException {
    public LineSuffixException() {
        super(HttpStatus.BAD_REQUEST, "-선으로 끝나는 이름을 입력해주세요.");
    }
}
