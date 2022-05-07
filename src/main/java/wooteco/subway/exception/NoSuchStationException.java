package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchStationException extends NoSuchElementException {

    public NoSuchStationException() {
        super("존재하지 않는 역입니다");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
