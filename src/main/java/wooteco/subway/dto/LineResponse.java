package wooteco.subway.dto;

import lombok.Getter;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.List;

@Getter
public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Line line, List<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
    }
}
