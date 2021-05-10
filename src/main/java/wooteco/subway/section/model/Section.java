package wooteco.subway.section.model;

import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.WrongDistanceException;
import wooteco.subway.station.model.Station;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateSection(upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSection(Long upStationId, Long downStationId, int distance) throws WrongDistanceException {
        validateSameStations(upStationId, downStationId);
        validateDistance(distance);
    }

    private void validateSameStations(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new DuplicationException("상행역과 하행역은 동일할 수 없습니다.");
        }
    }

    private void validateDistance(int distance) throws WrongDistanceException {
        if (distance <= 0) {
            throw new WrongDistanceException("");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
