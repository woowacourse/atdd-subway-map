package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateName(String name) {
        Objects.requireNonNull(name, "이름은 Null 일 수 없습니다.");
        validateNameLength(name);
    }

    private void validateNameLength(String name) {
        int length = name.length();
        if (length < 1 || length > 30) {
            throw new IllegalArgumentException("이름은 1~30 자 이내여야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Station station = (Station)o;
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

