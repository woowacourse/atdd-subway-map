package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

import javax.validation.constraints.Pattern;

public class StationRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*역$", message = "지하철 역 이름이 잘못되었습니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(name);
    }
}
