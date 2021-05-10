package wooteco.subway.controller.dto.request.station;

import javax.validation.constraints.NotBlank;

public class StationCreateRequestDto {
    @NotBlank
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
