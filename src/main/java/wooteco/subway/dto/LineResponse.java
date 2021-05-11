package wooteco.subway.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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
        return this.stations;
    }

    public static LineResponse from(Line line) {
        List<StationResponse> stationResponses = line.getSections().sections()
                .stream()
                .map(Section::getUpStation)
                .map(StationResponse::from)
                .collect(Collectors.toList());

        List<Section> sections = line.getSections().sections();
        int size = sections.size();
        stationResponses.add(StationResponse.from(sections.get(size-1).getDownStation()));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }
}
