package wooteco.subway.section;

import wooteco.subway.exception.SubWayException;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateIfDownStationSameAsUpStation(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Section section) {
        this(id, lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, null, upStationId, downStationId, distance);
    }

    private void validateIfDownStationSameAsUpStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SubWayException("구간의 상행과 하행이 같을 수 없습니다.");
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

    public boolean isSameUp(Long upStationId) {
        return this.upStationId.equals(upStationId);
    }

    public boolean isSameDown(Long downStationId) {
        return this.downStationId.equals(downStationId);
    }

    public boolean hasLongerDistanceThan(Section oldSection) {
        return this.distance >= oldSection.getDistance();
    }
}
