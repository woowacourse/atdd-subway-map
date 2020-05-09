package wooteco.subway.admin.station.service.dto;


import wooteco.subway.admin.station.domain.Station;

public class StationCreateRequest {
    private String name;

    public StationCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
