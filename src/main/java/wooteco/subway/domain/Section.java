package wooteco.subway.domain;

import wooteco.subway.exception.IllegalSectionException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

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
            throw new IllegalSectionException("구간 사이의 거리는 0보다 작거나 같을 수 없습니다.");
        }
    }

    private void checkSameStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalSectionException("구간의 두 역이 같을 수 없습니다.");
        }
    }

    public boolean containsStation(Section section) {
        return upStationId.equals(section.getDownStationId()) || downStationId.equals(section.getUpStationId());
    }

    public boolean isFork(Section section) {
        return upStationId.equals(section.getUpStationId()) || downStationId.equals(section.getDownStationId());
    }

    public boolean isSameSection(Section section) {
        return (upStationId.equals(section.getUpStationId()) || upStationId.equals(section.getDownStationId()))
                && (downStationId.equals(section.getUpStationId()) || downStationId.equals(section.getDownStationId()));
    }

    public boolean isSameUpStation(Long stationId) {
        return getUpStationId().equals(stationId);
    }

    public boolean isSameDownStation(Long stationId) {
        return getDownStationId().equals(stationId);
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
