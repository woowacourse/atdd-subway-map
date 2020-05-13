package wooteco.subway.admin.utils;

import wooteco.subway.admin.exception.InvalidNameException;

public class Validator {
    private static final String BLANK = " ";

    public static void validateNotEmpty(String name) {
        if (name.isEmpty()) {
            throw new InvalidNameException("이름은 비어있을 수 없습니다.");
        }
    }

    public static void validateNotContainsBlank(String name) {
        if (name.contains(BLANK)) {
            throw new InvalidNameException("이름은 공백이 포함될 수 없습니다.");
        }
    }
}
