package wooteco.subway.exception;

public class Validator {

    public static void requireNonNull(final Object object) {
        if (object == null) {
            throw new InvalidRequestException("입력에 공백이 있을 수 없습니다.");
        }
    }
}
