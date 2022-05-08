package wooteco.subway.dto;

import java.util.List;
import lombok.Getter;
import wooteco.subway.domain.Line;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line, List<StationResponse> stations) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        this.stations = stations;
    }
}
