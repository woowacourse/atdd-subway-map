package wooteco.subway.exception;

public class StationDeleteFailureException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "지하철역 삭제에 실패하였습니다";

    public StationDeleteFailureException(Long id) {
        super(DEFAULT_MESSAGE + " : " + id);
    }
}
