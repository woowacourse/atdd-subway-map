package wooteco.subway.controller.dto.request.station;

public class StationCreateRequestDto {
    private String name;

    public StationCreateRequestDto() {
    }

    public StationCreateRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
