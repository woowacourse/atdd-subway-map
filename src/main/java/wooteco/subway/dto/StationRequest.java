package wooteco.subway.dto;

import wooteco.subway.domain.station.Station;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank(message = "유효하지 않은 역 이름입니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station createStation() {
        return new Station(this.name);
    }

    public String getName() {
        return name;
    }
}
