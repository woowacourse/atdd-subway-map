package wooteco.subway.dto;

import java.util.List;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

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

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public LineResponse() {
        this(null, null, null, null);
    }

    public static LineResponse from(Line line) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor()
        );
    }

    public static LineResponse of(Line line, Section section) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            List.of(StationResponse.from(section.getUpStation()), StationResponse.from(section.getDownStation()))
        );
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
