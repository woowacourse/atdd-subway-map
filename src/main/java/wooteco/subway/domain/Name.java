package wooteco.subway.domain;

public class Name {
    private static final String ERROR_NULL = "이름은 null이 될 수 없습니다.";
    private static final String ERROR_EMPTY = "이름에 공백이 포함될 수 없습니다.";
    private static final String ERROR_SPECIAL_CHAR = "이름에 특수문자가 포함될 수 없습니다.";
    private static final String REGEX = ".*[^0-9a-zA-Zㄱ-ㅎ가-힣_]+.*";

    private final String name;

    public Name(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        checkNull(name);
        checkEmpty(name);
        checkSpecialChar(name);
    }

    private void checkNull(String name) {
        if (name == null) {
            throw new IllegalArgumentException(ERROR_NULL);
        }
    }

    private void checkEmpty(String name) {
        if (name.contains(" ")) {
            throw new IllegalArgumentException(ERROR_EMPTY);
        }
    }

    private void checkSpecialChar(String name) {
        if (name.matches(REGEX)) {
            throw new IllegalArgumentException(ERROR_SPECIAL_CHAR);
        }
    }
}
