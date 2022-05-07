package wooteco.subway.service.dto.station;

public class StationResponseDto {

    private final Long id;
    private final String name;

    public StationResponseDto(Long id, String name) {
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
