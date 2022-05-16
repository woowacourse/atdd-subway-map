package wooteco.subway.exception.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class IllegalMergeSectionException extends SubwayException {
    private static final String MESSAGE = "합칠 수 없는 구간입니다.";

    public IllegalMergeSectionException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
