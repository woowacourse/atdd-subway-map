package wooteco.subway.service.dto.line;

import java.util.List;
import wooteco.subway.domain.Station;

public class LineFindResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public LineFindResponse(Long id, String name, String color, List<Station> stations) {
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

    public List<Station> getStations() {
        return stations;
    }
}
