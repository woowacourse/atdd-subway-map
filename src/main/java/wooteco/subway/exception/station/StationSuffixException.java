package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class StationSuffixException extends SubwayException {
    public StationSuffixException() {
        super("-역으로 끝나는 이름을 입력해주세요.");
    }
}
