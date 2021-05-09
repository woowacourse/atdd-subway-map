package wooteco.subway.domain;

public class Distance {

    private static final int VALUE_MIN = 0;

    private final int value;

    public Distance(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (VALUE_MIN >= value) {
            throw new IllegalArgumentException("거리 값은 자연수 입니다.");
        }
    }

    public int value() {
        return value;
    }
}
