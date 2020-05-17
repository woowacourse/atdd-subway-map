package wooteco.subway.admin.exception;

import wooteco.subway.admin.domain.LineStation;

public class DuplicatedLineStationException extends RuntimeException {
    public DuplicatedLineStationException(LineStation station) {
        super("이미 추가된 구간: " + station.getPreStationId() + " -> " + station.getStationId());
    }
}
