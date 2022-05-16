package wooteco.subway.domain.property;

import java.util.Objects;

import wooteco.subway.exception.InvalidRequestException;

public class Name {

    private final String value;

    public Name(String value) {
        validatePresent(value);
        this.value = value;
    }

    private void validatePresent(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException("이름은 필수 입력값입니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Name name = (Name)o;
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
