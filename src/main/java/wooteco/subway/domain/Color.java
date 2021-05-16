package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.NullColorException;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Color color = (Color) o;
        return Objects.equals(value, color.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
