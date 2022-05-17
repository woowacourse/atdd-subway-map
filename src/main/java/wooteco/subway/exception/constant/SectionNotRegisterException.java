package wooteco.subway.exception.constant;

public class SectionNotRegisterException extends IllegalArgumentException {

    public static final String MESSAGE = "구간을 등록할 수 없습니다.";

    public SectionNotRegisterException() {
        super(MESSAGE);
    }
}
