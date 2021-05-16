package wooteco.subway.exception.line;

import wooteco.subway.exception.InvalidRequestException;

public class StationUpAndDownDuplicatedException extends InvalidRequestException {
    private static final String MESSAGE = "상하행역이 같습니다.";

    public StationUpAndDownDuplicatedException() {
        super(MESSAGE);
    }
}
