package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private static final int NAME_SIZE_LIMIT = 255;
    private static final String ERROR_MESSAGE_NAME_SIZE = "존재할 수 없는 이름입니다.";

    private Long id;
    private final String name;

    public Station(Long id, String name) {
        validateNameSize(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        validateNameSize(name);
        this.name = name;
    }

    private void validateNameSize(String name) {
        if (name == null || name.isBlank() || name.length() > NAME_SIZE_LIMIT) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NAME_SIZE);
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
        if (!(o instanceof Station))
            return false;
        Station station = (Station)o;
        return Objects.equals(this.id, station.id) && Objects.equals(this.name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

