package wooteco.subway.entity;

import java.util.Objects;
import wooteco.subway.domain.station.RegisteredStation;

public class RegisteredStationEntity {

    private final StationEntity stationEntity;
    private final LineEntity lineEntity;

    public RegisteredStationEntity(StationEntity stationEntity, LineEntity lineEntity) {
        this.stationEntity = stationEntity;
        this.lineEntity = lineEntity;
    }

    public RegisteredStation toDomain() {
        return new RegisteredStation(lineEntity.toDomain(), stationEntity.toDomain());
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
