package wooteco.subway.station.service.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.domain.Station;

public class StationDto {

    private final Long id;
    private final String name;

    private StationDto(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationDto of(final Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public static List<StationDto> toListDto(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationDto(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
