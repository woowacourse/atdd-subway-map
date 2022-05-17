package wooteco.subway.repository.exception;

import java.util.NoSuchElementException;

public class NoSuchLineException extends NoSuchElementException {

    public NoSuchLineException(Long id) {
        super(String.format("지하철노선을 찾을 수 없습니다. [id : %d]", id));
    }
}
