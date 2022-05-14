package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private String name;

    private Station() {
    }

    public Station(String name) {
        validateNameNotEmpty(name);
        this.name = name;
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    private void validateNameNotEmpty(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

