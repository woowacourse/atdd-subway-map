package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionDeleteException extends SubwayLineControlException {

    public SectionDeleteException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
