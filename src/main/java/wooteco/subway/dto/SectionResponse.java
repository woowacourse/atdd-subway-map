package wooteco.subway.dto;

import wooteco.subway.domain.section.Section;

public class SectionResponse {

    private Long id;
    private StationResponse upStationResponse;
    private StationResponse downStationResponse;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long id, StationResponse upStationResponse,
        StationResponse downStationResponse, int distance) {
        this.id = id;
        this.upStationResponse = upStationResponse;
        this.downStationResponse = downStationResponse;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStationResponse() {
        return upStationResponse;
    }

    public StationResponse getDownStationResponse() {
        return downStationResponse;
    }

    public int getDistance() {
        return distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
            section.getId(),
            StationResponse.of(section.getUpStation()),
            StationResponse.of(section.getDownStation()),
            section.getDistance()
        );
    }
}
