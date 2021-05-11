package wooteco.subway.section.dto;

import wooteco.subway.section.Section;
import wooteco.subway.station.dto.StationResponse;

public class AddSectionDto {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private Integer distance;

    public AddSectionDto() {
    }

    public AddSectionDto(Long id, StationResponse upStation, StationResponse downStation, Integer distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section toEntity() {
        return new Section(id, upStation.getId(), downStation.getId(), distance);
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }
}
