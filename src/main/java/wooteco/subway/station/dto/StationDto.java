package wooteco.subway.station.dto;

public class StationDto {

    private final Long id;
    private final String name;

    public StationDto(Long id) {
        this(id, null);
    }

    public StationDto(String name) {
        this(null, name);
    }

    public StationDto(Long id, String name) {
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
