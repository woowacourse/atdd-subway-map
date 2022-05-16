package wooteco.subway.domain;

public class Name {

    private static final String ERROR_EMPTY = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";
    private static final String BLANK = " ";
    private static final String ERROR_INCLUDE_BLANK = "[ERROR] 이름은 내부에 공백이 포함될 수 없습니다.";
    private static final String ERROR_INVALID_LENGTH = "[ERROR] 이름은 2글자 이상이어야합니다.";
    private static final int INVALID_NAME_LENGTH = 2;
    private static final String ERROR_SPECIAL = "[ERROR] 이름에 특수문자를 입력할 수 없습니다.";
    private static final String REGEX_SPECIAL = "[가-힣\\w_]+";

    private final String value;

    public Name(final String value) {
        validateName(value);
        this.value = value;
    }

    private void validateName(final String name) {
        checkEmpty(name);
        checkIncludeBlank(name);
        checkValidLengthOfName(name);
        checkSpecialCharInName(name);
    }

    private void checkEmpty(final String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMPTY);
        }
    }

    private void checkIncludeBlank(final String name) {
        if (name.trim().contains(BLANK)) {
            throw new IllegalArgumentException(ERROR_INCLUDE_BLANK);
        }
    }

    private void checkValidLengthOfName(final String name) {
        int nameLength = name.length();
        if (INVALID_NAME_LENGTH > nameLength) {
            throw new IllegalArgumentException(ERROR_INVALID_LENGTH);
        }
    }

    private void checkSpecialCharInName(final String name) {
        if (!name.matches(REGEX_SPECIAL)) {
            throw new IllegalArgumentException(ERROR_SPECIAL);
        }
    }

    public String getValue() {
        return value;
    }
}
