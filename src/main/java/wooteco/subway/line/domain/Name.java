package wooteco.subway.line.domain;

import java.util.Objects;
import wooteco.subway.exception.NullNameException;

public class Name {
    private final String value;

    public Name(final String value) {
        this.value = value;
        validateNull(this.value);
    }

    private void validateNull(final String value) {
        if (Objects.isNull(value)) {
            throw new NullNameException();
        }
    }

    public String value() {
        return value;
    }
}
