package wooteco.subway.domain;

import wooteco.subway.exception.IllegalSectionException;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateSection(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSection(Long upStationId, Long downStationId, int distance) {
        checkNegativeDistance(distance);
        checkSameStation(upStationId, downStationId);
    }

    private void checkNegativeDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("두 종점간의 거리는 0보다 작거나 같을 수 없습니다.");
        }
    }

    private void checkSameStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalSectionException("두 종점이 같을 수 없습니다.");
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
