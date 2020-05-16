package wooteco.subway.admin.dto;

import java.util.Set;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponseCreateDto {
    private Line line;
    private Set<Station> stations;

    private LineResponseCreateDto(Line line, Set<Station> stations) {
        this.line = line;
        this.stations = stations;
    }

    public static LineResponseCreateDto of(Line line, Set<Station> stations) {
        return new LineResponseCreateDto(line, stations);
    }

    public Line getLine() {
        return line;
    }

    public Set<Station> getStations() {
        return stations;
    }
}
