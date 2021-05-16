package wooteco.subway.exception;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoSuchLineException extends EmptyResultDataAccessException {
    private static String msg = "해당 id에 맞는 노선을 찾을 수 없습니다.";

    public NoSuchLineException(int expectedSize) {
        super(msg, expectedSize);
    }
}
