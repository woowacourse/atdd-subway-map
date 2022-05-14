package wooteco.subway.dto;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(StationDto stationDto) {
        this.id = stationDto.getId();
        this.name = stationDto.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
