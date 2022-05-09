package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchStationException extends NoSuchElementException {

    public NoSuchStationException() {
        super("[ERROR] 역이 존재하지 않습니다.");
    }
}
