package wooteco.subway.domain.entity;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionEntity {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionEntity(Long id, Long lineId, Long upStationId, Long downStationId, Long distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionEntity(Long lineId, Long upStationId, Long downStationId, Long distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Long getDistance() {
        return distance;
    }
}
