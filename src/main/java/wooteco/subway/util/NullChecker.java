package wooteco.subway.util;

public class NullChecker {

    public static void validateInputsNotNull(Object...inputs) {
        for (Object input : inputs) {
            validateNotEmpty(input);
        }
    }

    private static void validateNotEmpty(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("빈 필수값이 있습니다.");
        }
    }

}
