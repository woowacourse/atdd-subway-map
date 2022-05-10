package wooteco.subway.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class LineResponseDto {

    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    public LineResponseDto() {
    }

    public LineResponseDto(Long id, String name, String color, List<StationResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    // 부생성자로 바꾸기
    public static LineResponseDto of(final Line line) {
        final List<StationResponseDto> stationResponseDtos = convertToStationResponseDtos(line.getStations());
        return new LineResponseDto(line.getId(), line.getName(), line.getColor(), stationResponseDtos);
    }

    private static List<StationResponseDto> convertToStationResponseDtos(final List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponseDto(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponseDto> getStations() {
        return stations;
    }
}
