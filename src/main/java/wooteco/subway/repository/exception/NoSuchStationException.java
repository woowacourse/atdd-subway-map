package wooteco.subway.repository.exception;

import java.util.NoSuchElementException;

public class NoSuchStationException extends NoSuchElementException {

    public NoSuchStationException(Long id) {
        super(String.format("지하철역을 찾을 수 없습니다. [id : %d]", id));
    }
}
