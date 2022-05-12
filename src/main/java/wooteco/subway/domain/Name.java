package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.IllegalInputException;

public class Name {

    private static final int MAX_NAME_LENGTH = 15;

    private final String value;

    public Name(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (value.isBlank()) {
            throw new IllegalInputException("이름이 공백이 되어서는 안됩니다.");
        }
        if (value.length() > MAX_NAME_LENGTH) {
            throw new IllegalInputException("이름이 " + MAX_NAME_LENGTH + "자를 넘어서는 안됩니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Name name = (Name) o;
        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Name{" +
                "value='" + value + '\'' +
                '}';
    }
}
