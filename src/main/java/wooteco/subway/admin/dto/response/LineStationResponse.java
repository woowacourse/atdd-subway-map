package wooteco.subway.admin.dto.response;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.util.Set;

public class LineStationResponse {
    private Long id;
    private String name;
    private Set<Station> stations;

    public LineStationResponse() {
    }

    public LineStationResponse(Long id, String name, Set<Station> stations) {
        this.id = id;
        this.name = name;
        this.stations = stations;
    }

    public LineStationResponse(Line line, Set<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Station> getStations() {
        return stations;
    }
}
