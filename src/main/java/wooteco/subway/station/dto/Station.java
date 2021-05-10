package wooteco.subway.station.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class Station {
    private Long id;
    @NotBlank
    @Pattern(regexp = "^[가-힣|0-9]*역$")
    private String name;

    public Station() {
    }

    public Station(wooteco.subway.station.domain.Station station) {
        this(station.getId(), station.getName());
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, String name) {
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
