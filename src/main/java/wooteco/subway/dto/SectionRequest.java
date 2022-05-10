package wooteco.subway.dto;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {

        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Section toEntity(Long lineId) {
        return new Section(lineId, new
                Station(upStationId, ""),
                new Station(downStationId, ""), distance);
    }
}
