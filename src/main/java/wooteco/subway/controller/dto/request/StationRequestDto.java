package wooteco.subway.controller.dto.request;

public class StationRequestDto {

    private String name;

    public StationRequestDto() {
    }

    public StationRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
