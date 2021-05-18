package wooteco.subway.line;

import java.util.Objects;
import wooteco.subway.exception.line.NullColorException;

public class Color {
    private final String value;

    public Color(final String value) {
        this.value = value;
        validateNull(this.value);
    }

    private void validateNull(final String value) {
        if (Objects.isNull(value)) {
            throw new NullColorException();
        }
    }

    public String value() {
        return value;
    }
}
