package wooteco.subway.exception;

public class LineDeleteFailureException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "노선 삭제에 실패하였습니다";

    public LineDeleteFailureException() {
        super(DEFAULT_MESSAGE);
    }

    public LineDeleteFailureException(Long id) {
        super(DEFAULT_MESSAGE + " : " + id);
    }
}
