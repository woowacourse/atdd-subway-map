package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private static final int NAME_LENGTH = 255;

    private final Long id;
    private final String name;

    public Station() {
        this(null, null);
    }

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("역이름은 비어있을 수 없습니다.");
        }
        if (name.length() > NAME_LENGTH) {
            throw new IllegalArgumentException("역이름은 " + NAME_LENGTH + "자를 초과할 수 없습니다.");
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
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

