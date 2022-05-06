package wooteco.subway.exception.line;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class InvalidLineIdException extends InvalidSubwayResourceException {

    private static final String MESSAGE = "존재하지 않는 노선입니다.";

    public InvalidLineIdException() {
        super(MESSAGE);
    }
}
