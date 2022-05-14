package wooteco.subway.domain2.section;

import java.util.Objects;
import wooteco.subway.domain2.station.Station;
import wooteco.subway.entity.SectionEntity2;
import wooteco.subway.entity.StationEntity;

public class Section {

    private static final String SAME_STATION_INPUT_EXCEPTION = "서로 다른 두 개의 역을 입력해야 합니다.";
    private static final int MIN_DISTANCE = 1;

    private final Station upStation;
    private final Station downStation;
    private final int distance;

    private Section(Station upStation,
                    Station downStation,
                    int distance) {
        validateSection(upStation, downStation, distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(StationEntity upStation,
                             StationEntity downStation,
                             int distance) {
        return new Section(upStation.toDomain(), downStation.toDomain(), distance);
    }

    private void validateSection(Station upStation, Station downStation, int distance) {
        validateStations(upStation, downStation);
        validateDistance(distance);
    }

    private void validateStations(Station upStation, Station downStation) {
        validateNotNull(upStation);
        validateNotNull(downStation);
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_INPUT_EXCEPTION);
        }
    }

    private void validateNotNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("필요한 정보가 입력되지 않았습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("구간의 길이는 1이상이어야 합니다.");
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public SectionEntity2 toEntity(Long lineId) {
        return new SectionEntity2(lineId, upStation.toEntity(), downStation.toEntity(), distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance
                && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
