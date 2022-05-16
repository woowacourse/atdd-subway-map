package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private String name;

    private Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "[ERROR] 잘못된 입력값입니다. cause = name");
    }

    public Station(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
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
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

