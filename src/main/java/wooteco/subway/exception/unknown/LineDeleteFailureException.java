package wooteco.subway.exception.unknown;

import wooteco.subway.exception.SubwayUnknownException;

public class LineDeleteFailureException extends SubwayUnknownException {

    private static final String DEFAULT_MESSAGE = "노선 삭제에 실패하였습니다";

    public LineDeleteFailureException(Long id) {
        super(DEFAULT_MESSAGE + " : " + id);
    }
}
