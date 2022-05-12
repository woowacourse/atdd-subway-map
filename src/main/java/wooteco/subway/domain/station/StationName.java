package wooteco.subway.domain.station;

import java.util.Objects;

public class StationName {

    private final String name;

    public StationName(String name) {
        validateNameNotBlank(name);
        this.name = name;
    }

    private void validateNameNotBlank(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("지하철역 이름은 공백이 될 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationName that = (StationName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "StationName{" +
                "name='" + name + '\'' +
                '}';
    }
}
