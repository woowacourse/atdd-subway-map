package wooteco.subway.controller.web.line;

import wooteco.subway.controller.web.station.StationResponse;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.Collections;
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
        this(id, name, color, Collections.emptyList());
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName().text(), line.getColor().text(), convertDto(line.getSections()));
    }

    private static List<StationResponse> convertDto(Sections orderedSections) {
        List<StationResponse> stationResponses = orderedSections.getSections().stream()
                .map(Section::getUpStation)
                .map(StationResponse::of)
                .collect(Collectors.toList());
        StationResponse lastDownStation = getLastDownStation(orderedSections.getSections());
        stationResponses.add(lastDownStation);
        return stationResponses;
    }

    private static StationResponse getLastDownStation(List<Section> sections) {
        return StationResponse.of(sections.get(sections.size() - 1).getDownStation());
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
