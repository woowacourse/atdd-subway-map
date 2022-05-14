package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.MaxNameLengthException;

public class Name {

    public static final int MAX_LENGTH = 255;
    private final String value;

    public Name(final String value) {
        validateNameLength(value);
        this.value = value;
    }

    private void validateNameLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new MaxNameLengthException(); // 테스트 작성하기
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
        if (!(o instanceof Name)) {
            return false;
        }
        final Name name = (Name) o;
        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
