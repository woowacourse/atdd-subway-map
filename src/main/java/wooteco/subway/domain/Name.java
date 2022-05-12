package wooteco.subway.domain;

import java.util.Objects;

public class Name {

	private final String value;

	public Name(String value) {
		validate(value);
		this.value = value;
	}

	private void validate(String value) {
		if (value.isBlank()) {
			throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
		}
	}

	public boolean isSame(String value) {
		return this.value.equals(value);
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
		return Objects.equals(getValue(), name.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getValue());
	}

	@Override
	public String toString() {
		return "Name{" +
			"value='" + value + '\'' +
			'}';
	}
}
