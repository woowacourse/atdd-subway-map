package wooteco.subway.exception.unknown;

import wooteco.subway.exception.SubwayUnknownException;

public class StationDeleteFailureException extends SubwayUnknownException {

    private static final String DEFAULT_MESSAGE = "지하철역 삭제에 실패하였습니다";

    public StationDeleteFailureException(Long id) {
        super(DEFAULT_MESSAGE + " : " + id);
    }
}
