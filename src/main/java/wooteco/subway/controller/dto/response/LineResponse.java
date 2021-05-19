package wooteco.subway.controller.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line, Sections sections) {
        List<StationResponse> stationResponses = sections.getStations()
            .stream()
            .map(station -> StationResponse.of(station))
            .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
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
