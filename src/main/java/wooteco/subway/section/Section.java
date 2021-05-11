package wooteco.subway.section;

import wooteco.subway.exception.SubwayException;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section() {
    }

    public Section(Long id, Section section) {
        this(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isSameUpStation(Long stationId) {
        return this.upStationId.equals(stationId);
    }

    public boolean isSameDownStation(Long stationId) {
        return this.downStationId.equals(stationId);
    }

    public void updateDistance(Integer distance) {
        if (this.distance <= distance) {
            throw new SubwayException("넣을 수 없는 거리입니다.");
        }
        this.distance = this.distance - distance;
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

    public Integer getDistance() {
        return distance;
    }
}
