package wooteco.subway.exception.duplicateException;

public class LineDuplicationException extends RuntimeException {

    private static final String LINE_DUPLICATE = "이미 등록된 노선입니다.";

    public LineDuplicationException() {
        super(LINE_DUPLICATE);
    }
}
