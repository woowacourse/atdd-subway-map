package wooteco.subway.exception;

public class LineDuplicationException extends RuntimeException {

    private static final String message = "이미 등록된 노선입니다.";

    public LineDuplicationException() {
        super(message);
    }
}
