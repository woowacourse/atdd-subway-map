package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        validateNotNull(name, "name");
        this.id = id;
        this.name = name;
    }

    private void validateNotNull(String input, String param) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(String.format("%s은 필수 입력값입니다.", param));
        }
    }

    public Station(String name) {
        this(null, name);
    }

    public boolean hasSameNameWith(Station otherStation) {
        return this.name.equals(otherStation.name);
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
}

