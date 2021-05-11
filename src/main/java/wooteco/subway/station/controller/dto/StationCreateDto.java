package wooteco.subway.station.controller.dto;

public class StationCreateDto {

    private final String name;

    private StationCreateDto(final String name) {
        this.name = name;
    }

    public static StationCreateDto of(final String name) {
        return new StationCreateDto(name);
    }

    public String getName() {
        return name;
    }
}
