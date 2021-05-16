package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Station {
    private Long id;
    private String name;

    public static Station create(String name) {
        return create(null, name);
    }

    public static Station create(Long id, String name) {
        return new Station(id, name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
