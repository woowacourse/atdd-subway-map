package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineNameNullException extends LineException{
    public LineNameNullException() {
        super(HttpStatus.BAD_REQUEST, "노선 이름은 필수로 입력 해야 합니다.");
    }
}
