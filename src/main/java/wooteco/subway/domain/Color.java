package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.line.NullColorException;

public class Color {

    private final String value;

    public Color(String value) {
        this.value = value;
        validateNull(this.value);
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new NullColorException();
        }
    }

    public String getValue() {
        return value;
    }
}
