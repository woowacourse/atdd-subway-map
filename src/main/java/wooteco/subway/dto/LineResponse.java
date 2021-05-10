package wooteco.subway.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>(stations);
    }

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public LineResponse(Line line, Section section) {
        this(line.getId(), line.getName(), line.getColor(),
            Arrays.asList(new StationResponse(section.getUpStation()),
                new StationResponse(section.getDownStation())));
    }

    public LineResponse(Line line, List<Station> pathInLine) {
        this(line.getId(),
            line.getName(),
            line.getColor(),
            stationResponses(pathInLine));
    }

    private static List<StationResponse> stationResponses(List<Station> stations) {
        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
