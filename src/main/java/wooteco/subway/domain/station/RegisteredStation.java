package wooteco.subway.domain.station;

import java.util.Objects;
import wooteco.subway.domain.line.Line;

public class RegisteredStation {

    private final Line line;
    private final Station station;

    public RegisteredStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Line getRegisteredLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisteredStation that = (RegisteredStation) o;
        return Objects.equals(line, that.line)
                && Objects.equals(station, that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, station);
    }

    @Override
    public String toString() {
        return "RegisteredStation{" +
                "line=" + line +
                ", station=" + station +
                '}';
    }
}
