package wooteco.subway.entity;

import java.util.Objects;
import wooteco.subway.domain.Section;

public class SectionViewEntity {

    private final StationEntity upStation;
    private final StationEntity downStation;

    public SectionViewEntity(StationEntity upStation,
                             StationEntity downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public StationEntity getUpStation() {
        return upStation;
    }

    public StationEntity getDownStation() {
        return downStation;
    }

    public Section toSection() {
        return new Section(upStation.getId(), downStation.getId());
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
        return Objects.equals(upStation, that.upStation)
                && Objects.equals(downStation, that.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }

    @Override
    public String toString() {
        return "SectionViewEntity{" +
                "upStation=" + upStation +
                ", downStation=" + downStation +
                '}';
    }
}
