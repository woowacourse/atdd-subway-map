package wooteco.subway.station.controller.dto;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public StationCreateDto toStationCreateDto() {
        return StationCreateDto.of(name);
    }

    public String getName() {
        return name;
    }
}
