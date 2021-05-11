package wooteco.subway.domain.station.value;

import java.util.Objects;

public class StationName {

    private final String name;

    public StationName(String name) {
        validateStationNameSize(name);
        this.name = name;
    }

    private void validateStationNameSize(String name) {
        if(name.isEmpty()) {
            throw new IllegalArgumentException("글자 수는 0보다 커야 합니다.");
        }
    }

    public String asString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationName that = (StationName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
