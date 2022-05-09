package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchSectionException extends NoSuchElementException {

    public NoSuchSectionException() {
        super("존재하지 않는 구간입니다");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
