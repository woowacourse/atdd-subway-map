package wooteco.subway.station.dto;

public class StationDto {

    private final Long id;
    private final String name;

    public StationDto(final Long id) {
        this(id, null);
    }

    public StationDto(final String name) {
        this(null, name);
    }

    public StationDto(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
