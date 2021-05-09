package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.NullIdException;

public class Id {

    private final Long value;

    public Id(final Long value) {
        this.value = value;
        validateNull(this.value);
    }

    private void validateNull(final Long value) {
        if (Objects.isNull(value)) {
            throw new NullIdException();
        }
    }

    public Long getValue() {
        return value;
    }
}
