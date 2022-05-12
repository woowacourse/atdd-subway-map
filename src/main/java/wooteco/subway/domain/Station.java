package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
        validateField(name);
    }

    private void validateField(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역의 이름은 빈 값이면 안됩니다.");
        }
    }

    public Station(String name) {
        this(0L, name);
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
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

