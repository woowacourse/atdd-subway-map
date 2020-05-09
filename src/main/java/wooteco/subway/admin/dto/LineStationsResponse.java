package wooteco.subway.admin.dto;

import java.util.List;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineStationsResponse {
    private Long id;
    private String name;
    private String bgColor;
    private List<LineStationResponse> stations;

    public LineStationsResponse() {
    }

    public LineStationsResponse(Long id, String name, String bgColor,
            List<LineStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.bgColor = bgColor;
        this.stations = stations;
    }

    public static LineStationsResponse of(Line line, List<Station> stations) {
        return new LineStationsResponse(line.getId(), line.getName(), line.getBgColor(),
                LineStationResponse.listOf(line.getStations(), stations));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBgColor() {
        return bgColor;
    }

    public List<LineStationResponse> getStations() {
        return stations;
    }
}
