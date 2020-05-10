package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Station;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class StationCreateRequest {
    @NotBlank
    @Pattern(regexp = "^[^0-9].*")
    @Pattern(regexp = "[^\\s]*")
    private String name;

    public StationCreateRequest() {
    }

    public StationCreateRequest(String name) {
        this.name = name;
    }

    public Station toStation() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
