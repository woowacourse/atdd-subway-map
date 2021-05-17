package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

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

    public static StationDto toDto(Station station) {
        return new StationDto(station.id(), station.name());
    }

    public static List<StationDto> toDtos(List<Station> stations) {
        return stations.stream()
                .map(StationDto::toDto)
                .collect(Collectors.toList());
    }

    public static Station toStation(StationDto stationDto) {
        return new Station(stationDto.getId(), stationDto.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
