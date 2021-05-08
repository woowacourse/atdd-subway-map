package wooteco.subway.line.dto;

import wooteco.subway.line.entity.SectionEntity;

public class SectionAddRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionAddRequest() {
    }

    public SectionAddRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public void setUpStationId(final Long upStationId) {
        this.upStationId = upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public void setDownStationId(final Long downStationId) {
        this.downStationId = downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(final int distance) {
        this.distance = distance;
    }

    public SectionEntity toEntity(Long lineId) {
        return new SectionEntity(lineId, upStationId, downStationId, distance);
    }
}
