package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class LineSuffixException extends SubwayException {
    public LineSuffixException() {
        super(HttpStatus.BAD_REQUEST, "-선으로 끝나는 이름을 입력해주세요.");
    }
}
