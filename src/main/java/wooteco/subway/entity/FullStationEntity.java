package wooteco.subway.entity;

import java.util.Objects;

public class FullStationEntity {

    private final LineEntity lineEntity;
    private final StationEntity stationEntity;

    public FullStationEntity(LineEntity lineEntity, StationEntity stationEntity) {
        this.lineEntity = lineEntity;
        this.stationEntity = stationEntity;
    }

    public Long getId() {
        return lineEntity.getId();
    }

    public LineEntity getLineEntity() {
        return lineEntity;
    }

    public StationEntity getStationEntity() {
        return stationEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FullStationEntity that = (FullStationEntity) o;
        return Objects.equals(lineEntity, that.lineEntity)
                && Objects.equals(stationEntity, that.stationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineEntity, stationEntity);
    }

    @Override
    public String toString() {
        return "FullStationEntity{" +
                "lineEntity=" + lineEntity +
                ", stationEntity=" + stationEntity +
                '}';
    }
}
