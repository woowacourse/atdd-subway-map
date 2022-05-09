package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private static final String ERROR_NULL_OR_EMPTY = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";
    private static final String BLANK = " ";
    private static final String ERROR_INCLUDE_BLANK = "[ERROR] 이름은 내부에 공백이 포함될 수 없습니다.";
    private static final String ERROR_INVALID_LENGTH = "[ERROR] 이름은 2글자 이상이어야합니다.";
    private static final String REGEX_SPECIAL = "[가-힣\\w_]+";
    private static final String ERROR_SPECIAL = "[ERROR] 이름에 특수문자를 입력할 수 없습니다.";

    private Long id;
    private String name;

    public Station(final String name) {
        this(null, name);
    }

    public Station(final Long id, String name) {
        validateName(name);

        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        checkNullOrEmpty(name);
        checkIncludeBlank(name);
        checkValidLengthOfName(name);
        checkSpecialCharInName(name);
    }

    private void checkNullOrEmpty(final String name) {
        Objects.requireNonNull(name, ERROR_NULL_OR_EMPTY);
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_NULL_OR_EMPTY);
        }
    }

    private void checkIncludeBlank(final String name) {
        if (name.trim().contains(BLANK)) {
            throw new IllegalArgumentException(ERROR_INCLUDE_BLANK);
        }
    }

    private void checkValidLengthOfName(final String name) {
        int nameLength = name.length();
        if (2 > nameLength) {
            throw new IllegalArgumentException(ERROR_INVALID_LENGTH);
        }
    }

    private void checkSpecialCharInName(final String name) {
        if (!name.matches(REGEX_SPECIAL)) {
            throw new IllegalArgumentException(ERROR_SPECIAL);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

