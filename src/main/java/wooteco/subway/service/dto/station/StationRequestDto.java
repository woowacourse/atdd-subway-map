package wooteco.subway.service.dto.station;

public class StationRequestDto {

    private final String name;

    public StationRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
