package wooteco.subway.section.dto;

import wooteco.subway.section.Section;
import wooteco.subway.station.dto.StationResponse;

public class AddSectionForm {
    private long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public AddSectionForm() {
    }

    public AddSectionForm(long id, StationResponse upStation, StationResponse downStation, int distance) {
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

    public int getDistance() {
        return distance;
    }
}
