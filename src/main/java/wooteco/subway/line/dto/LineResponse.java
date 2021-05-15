package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this(line.id(), line.nameAsString(), line.color(), toStationsResponses(line.sections().sections()));
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
        return stations;
    }

    private static List<StationResponse> toStationsResponses(final List<Section> sections) {
        return sections.stream()
                .flatMap(section -> Stream.of(
                        section.upStation(), section.downStation()
                ))
                .distinct()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }
}
