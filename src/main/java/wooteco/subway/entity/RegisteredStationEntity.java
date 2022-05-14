package wooteco.subway.entity;

import java.util.Objects;

public class RegisteredStationEntity {

    private final StationEntity stationEntity;
    private final LineEntity lineEntity;

    public RegisteredStationEntity(StationEntity stationEntity, LineEntity lineEntity) {
        this.stationEntity = stationEntity;
        this.lineEntity = lineEntity;
    }

    public Long getId() {
        return lineEntity.getId();
    }

    public StationEntity getStationEntity() {
        return stationEntity;
    }

    public LineEntity getLineEntity() {
        return lineEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisteredStationEntity that = (RegisteredStationEntity) o;
        return Objects.equals(stationEntity, that.stationEntity)
                && Objects.equals(lineEntity, that.lineEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationEntity, lineEntity);
    }

    @Override
    public String toString() {
        return "RegisteredStationEntity{" +
                "stationEntity=" + stationEntity +
                ", lineEntity=" + lineEntity +
                '}';
    }
}
