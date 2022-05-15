package wooteco.subway.domain.line;

import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.station.Station;

public class SubwayMap {

    private final Line line;
    private final List<Station> stations;

    public SubwayMap(Line line, List<Station> stations) {
        this.line = line;
        this.stations = stations;
    }

    public Long getId() {
        return line.getId();
    }

    public String getName() {
        return line.getName();
    }

    public String getColor() {
        return line.getColor();
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubwayMap subwayMap = (SubwayMap) o;
        return Objects.equals(line, subwayMap.line)
                && Objects.equals(stations, subwayMap.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, stations);
    }

    @Override
    public String toString() {
        return "SubwayMap{" + "line=" + line + ", stations=" + stations + '}';
    }
}
