package wooteco.subway.dto;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section toEntity(final SectionRequest sectionRequest) {
        return new Section(
                new Station(sectionRequest.getUpStationId(), ""),
                new Station(sectionRequest.getDownStationId(), ""),
                sectionRequest.getDistance()
        );
    }

    public static SectionRequest from(final Section section) {
        return new SectionRequest(
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance()
        );
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
