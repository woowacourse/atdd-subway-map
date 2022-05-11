package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.NoSuchContentException;

public class NoSuchLineException extends NoSuchContentException {
    private static final String MESSAGE = "존재하지 않는 노선입니다.";

    public NoSuchLineException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
