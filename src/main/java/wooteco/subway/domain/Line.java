package wooteco.subway.domain;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public Line(Long id, String name, String color, List<Station> stations) {
        validateNameLength(name);
        validateColorLength(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Line(String name, String color) {
        this(null, name, color, null);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    private void validateNameLength(String name) {
        if (name.isBlank() || name.length() > 20) {
            throw new IllegalArgumentException("노선 이름은 최소 1글자이상 20글자 이하여야 합니다.");
        }
    }

    private void validateColorLength(String color) {
        if (color.isBlank() || color.length() > 30) {
            throw new IllegalArgumentException("노선 색상은 최소 1글자이상 30글자 이하여야 합니다.");
        }
    }

    public void deleteStation(Station station) {
        if (!containsStation(station)) {
            throw new NoSuchElementException("노선에 해당하는 역만 삭제할 수 있습니다.");
        }
        if (isDefaultSection()) {
            throw new IllegalStateException("마지막 구간은 삭제할 수 없습니다.");
        }
        stations.remove(station);
    }

    public boolean containsStation(Station another) {
        return stations.stream()
                .anyMatch(station -> station.equalsId(another));
    }

    public boolean isDefaultSection() {
        return stations.size() == 2;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
                && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
