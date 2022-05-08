package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.domain.exception.BlankArgumentException;

public class Station {

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        if (name.isBlank()) {
            throw new BlankArgumentException("지하철의 이름은 빈 문자열일 수 없습니다.");
        }
        this.id = id;
        this.name = name;
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
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

