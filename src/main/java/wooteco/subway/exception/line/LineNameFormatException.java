package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineNameFormatException extends LineException {
    public LineNameFormatException(String name) {
        super(HttpStatus.BAD_REQUEST, "\"" + name + "\"은 노선의 이름 패턴과 맞지 않습니다.");
    }
}
