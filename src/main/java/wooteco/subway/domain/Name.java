package wooteco.subway.domain;

public class Name {

    private final String value;

    public Name(String value) {
        validatePresent(value);
        this.value = value;
    }

    private void validatePresent(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이름은 필수 입력값입니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
