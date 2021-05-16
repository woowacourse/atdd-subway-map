package wooteco.subway.section.dto;

import wooteco.subway.line.dto.LineRequest;
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

    public SectionRequest(LineRequest lineRequest) {
        this(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public Section toEntity() {
        Station upStation = new Station(upStationId, null);
        Station downStation = new Station(downStationId, null);

        return new Section(upStation, downStation, distance);
    }
}
