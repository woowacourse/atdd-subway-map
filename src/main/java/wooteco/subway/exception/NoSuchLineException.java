package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchLineException extends NoSuchElementException {

    public NoSuchLineException() {
        super("[ERROR] 노선이 존재하지 않습니다");
    }
}
