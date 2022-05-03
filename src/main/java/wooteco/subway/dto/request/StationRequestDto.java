package wooteco.subway.dto.request;

public class StationRequestDto {

    private String name;

    public StationRequestDto() {
    }

    public StationRequestDto(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
