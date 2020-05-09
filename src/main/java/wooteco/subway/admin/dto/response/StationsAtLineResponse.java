package wooteco.subway.admin.dto.response;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.util.Set;

public class StationsAtLineResponse {
    private Long id; // lineId
    private String name; // lineName
    private Set<Station> stations;

    public StationsAtLineResponse() {
    }

    public StationsAtLineResponse(Long id, String name, Set<Station> stations) {
        this.id = id;
        this.name = name;
        this.stations = stations;
    }

    public StationsAtLineResponse(Line line, Set<Station> stations) {
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
