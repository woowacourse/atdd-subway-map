package wooteco.subway.domain;

import wooteco.subway.exception.DataLengthException;

import java.util.Objects;

public class Station {

    private static final int NAME_LENGTH = 20;

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        validateDataSize(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateDataSize(String name) {
        if (name.isBlank() || name.length() > NAME_LENGTH) {
            throw new DataLengthException("역 이름이 빈 값이거나 최대 범위(" + NAME_LENGTH + "를 초과했습니다.");
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
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(getId(), station.getId()) && Objects.equals(getName(), station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

