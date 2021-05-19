package wooteco.subway.section.dto;

import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

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

    public Section toEntity(Line newLine) {
        Station upStation = new Station(upStationId, null);
        Station downStation = new Station(downStationId, null);

        return new Section(newLine, upStation, downStation, distance);
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
