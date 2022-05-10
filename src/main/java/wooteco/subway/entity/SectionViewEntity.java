package wooteco.subway.entity;

import java.util.Objects;

public class SectionViewEntity {

    private final Long lineId;
    private final StationEntity upStation;
    private final StationEntity downStation;
    private final int distance;

    public SectionViewEntity(Long lineId,
                             StationEntity upStation,
                             StationEntity downStation,
                             int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionViewEntity that = (SectionViewEntity) o;
        return distance == that.distance
                && Objects.equals(lineId, that.lineId) && Objects.equals(
                upStation, that.upStation)
                && Objects.equals(downStation, that.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "SectionViewEntity{" +
                "lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
