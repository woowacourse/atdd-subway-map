package wooteco.subway.exception;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoSuchSectionException extends EmptyResultDataAccessException {
    private static String msg = "해당하는 역을 포함하는 구간이 없습니다.";

    public NoSuchSectionException(int expectedSize) {
        super(msg, expectedSize);
    }
}
