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

    public Long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Id id = (Id) o;
        return Objects.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
