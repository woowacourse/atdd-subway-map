package wooteco.subway.entity;

import java.util.Objects;

public class StationEntity {
    private Long id;
    private String name;

    public StationEntity() {
    }

    public StationEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationEntity(String name) {
        this.name = name;
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
        StationEntity stationEntity = (StationEntity) o;
        return name.equals(stationEntity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

