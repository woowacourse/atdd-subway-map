package wooteco.subway.exception;

public class LineUpdateFailureException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "노선 수정에 실패하였습니다";

    public LineUpdateFailureException() {
        super(DEFAULT_MESSAGE);
    }

    public LineUpdateFailureException(Long id) {
        super(DEFAULT_MESSAGE + " : " + id);
    }
}
