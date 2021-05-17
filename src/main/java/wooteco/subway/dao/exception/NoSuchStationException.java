package wooteco.subway.dao.exception;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoSuchStationException extends EmptyResultDataAccessException {
    private static String msg = "해당하는 id의 역이 없습니다.";

    public NoSuchStationException(int expectedSize) {
        super(msg, expectedSize);
    }
}
