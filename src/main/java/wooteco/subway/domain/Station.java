package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private static final int MAX_NAME_LENGTH = 255;

    private final Long id;
    private final String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        validateName(name);

        this.id = id;
        this.name = name;
    }

    private void validateName(String name) {
        Objects.requireNonNull(name);
        validateBlankName(name);
        validateNameLength(name);
    }

    private void validateBlankName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역 이름은 빈 문자열일 수 없습니다.");
        }
    }

    private void validateNameLength(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("역 이름은 %d자를 초과하면 안됩니다.", MAX_NAME_LENGTH));
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

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

