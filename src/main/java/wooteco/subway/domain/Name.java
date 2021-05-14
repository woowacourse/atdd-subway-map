package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.NullNameException;

public class Name {

    private final String value;

    public Name(String value) {
        this.value = value;
        validateNull(this.value);
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new NullNameException();
        }
    }

    public String getValue() {
        return value;
    }
}
