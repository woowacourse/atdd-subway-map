package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchLineException extends NoSuchElementException {
    public NoSuchLineException(Long lineId) {
        super("id가 " + lineId + "인 노선은 존재하지 않습니다.");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
