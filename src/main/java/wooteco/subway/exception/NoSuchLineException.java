package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchLineException extends NoSuchElementException {

    public NoSuchLineException() {
        super("존재하지 않는 노선입니다");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
