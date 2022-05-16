package wooteco.subway.exception.constant;

import lombok.AllArgsConstructor;

public class NotExistException extends IllegalArgumentException {

    private static final String MESSAGE = " 존재하지 않습니다.";

    public NotExistException(Which which) {
        super(which.MESSAGE + MESSAGE);
    }

    @AllArgsConstructor
    public enum Which {
        LINE("노선이"),
        STATION("역이");

        private final String MESSAGE;
    }
}
