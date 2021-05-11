package wooteco.subway.exceptions;

public class SectionNotFoundException extends RuntimeException {

    private static final String message = "일치하는 구간 찾을 수 없습니다.";

    public SectionNotFoundException() {
        super(message);
    }
}
