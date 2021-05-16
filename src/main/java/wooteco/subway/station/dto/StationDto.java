package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class StationDto {
    private Long id;
    @NotBlank
    @Pattern(regexp = "^[가-힣|0-9]*역$")
    private String name;

    public StationDto() {
    }

    public StationDto(String name) {
        this.name = name;
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
