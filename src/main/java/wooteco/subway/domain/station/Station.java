package wooteco.subway.domain.station;

import java.util.Objects;
import wooteco.subway.entity.StationEntity;

public class Station {

    private static final String INVALID_NAME_EXCEPTION = "역의 이름 정보가 입력되지 않았습니다.";

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(INVALID_NAME_EXCEPTION);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StationEntity toEntity() {
        return new StationEntity(id, name);
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
        return Objects.equals(id, station.id)
                && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Station{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
