package wooteco.subway.dto.response;

public class StationResponseDto {

    private final Long id;
    private final String name;

    public StationResponseDto(final Long id, final String name) {
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
