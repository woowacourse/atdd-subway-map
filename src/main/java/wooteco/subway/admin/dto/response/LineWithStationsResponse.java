package wooteco.subway.admin.dto.response;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public class LineWithStationsResponse {
    private Long id;
    private String name;
    private String bgColor;
    private List<Station> stations;

    public LineWithStationsResponse() {
    }

    public LineWithStationsResponse(Long id, String name, String bgColor, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.bgColor = bgColor;
        this.stations = stations;
    }

    public LineWithStationsResponse(Line line, List<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.bgColor = line.getBgColor();
        this.stations = stations;
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

    public List<Station> getStations() {
        return stations;
    }
}
