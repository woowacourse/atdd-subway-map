package wooteco.subway.station.domain;

import java.util.Objects;

public class Station {
    private final Long id;
    private final String name;

    public Station(final String name) {
        this(null, name);
    }

    public Station(final Long id, final String name) {
        validateStation(name);
        this.id = id;
        this.name = name;
    }

    private void validateStation(String name) {
        Objects.requireNonNull(name, "지하철 역의 이름은 null일 수 없습니다.");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("지하철 역의 이름은 빈 값일 수 없습니다.");
        }
    }

    public boolean isSameName(final String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

