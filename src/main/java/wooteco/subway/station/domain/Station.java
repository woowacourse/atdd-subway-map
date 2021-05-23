package wooteco.subway.station.domain;

import wooteco.subway.exception.StationException;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(0L, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean equalName(String name) {
        return this.name.equals(name);
    }

    public boolean equalId(Long id) {
        return this.id.equals(id);
    }

    private void validateName(String name) {
        if (name == null || name.length() == 0) {
            throw new StationException("유효하지 않는 역 이름입니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;

        Station station = (Station) o;

        if (!getId().equals(station.getId())) return false;
        return getName().equals(station.getName());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}

