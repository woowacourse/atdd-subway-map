package wooteco.subway.domain.section;

import lombok.Builder;
import lombok.Getter;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.Objects;
import java.util.stream.Stream;

@Builder
@Getter
public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;
    private Long lineId;

    public Section(Long id, int distance, Long lineId) {
        this(id, null, null, distance, lineId);
    }

    public Section(Station upStation, Station downStation, int distance, Long lineId) {
        this(null, upStation, downStation, distance, lineId);
    }

    public Section(Long id, Station upStation, Station downStation, int distance, Long lineId) {
        validateStations(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.lineId = lineId;
    }

    private void validateStations(Station upStation, Station downStation) {
        if (Objects.isNull(upStation) && Objects.isNull(downStation)) {
            return;
        }
        if (upStation.equals(downStation)) {
            throw new SubwayException(ExceptionStatus.INVALID_SECTION);
        }
    }

    public Section splitLongerSectionBy(Section shorterSection) {
        validateSplitCondition(shorterSection);
        int adjustedDistance = this.distance - shorterSection.distance;
        if (this.hasSameUpStation(shorterSection)) {
            return splitAfterShorterSection(shorterSection, adjustedDistance);
        }
        return splitBeforeShorterSection(shorterSection, adjustedDistance);
    }

    private void validateSplitCondition(Section shorterSection) {
        if (distance <= shorterSection.distance) {
            throw new SubwayException(ExceptionStatus.SECTION_NOT_ADDABLE);
        }
        if (this.hasSameUpStation(shorterSection) && this.hasSameDownStation(shorterSection)) {
            throw new SubwayException(ExceptionStatus.SECTION_NOT_ADDABLE);
        }
        if (upStation.notEquals(shorterSection.upStation) && downStation.notEquals(shorterSection.downStation)) {
            throw new SubwayException(ExceptionStatus.SECTION_NOT_ADDABLE);
        }
    }

    private Section splitAfterShorterSection(Section shorterSection, int adjustedDistance) {
        return Section.builder()
                .id(this.id)
                .upStation(shorterSection.downStation)
                .downStation(this.downStation)
                .distance(adjustedDistance)
                .lineId(this.lineId)
                .build();
    }

    private Section splitBeforeShorterSection(Section shorterSection, int adjustedDistance) {
        return Section.builder()
                .id(this.id)
                .upStation(this.upStation)
                .downStation(shorterSection.upStation)
                .distance(adjustedDistance)
                .lineId(this.lineId)
                .build();
    }

    public Section append(Section section) {
        if (!isConnectedTowardDownWith(section)) {
            throw new SubwayException(ExceptionStatus.SECTION_NOT_CONNECTABLE);
        }
        int adjustedDistance = this.distance + section.distance;
        return Section.builder()
                .upStation(this.upStation)
                .downStation(section.downStation)
                .distance(adjustedDistance)
                .lineId(this.lineId)
                .build();
    }

    public boolean isConnectedTowardDownWith(Section nextSection) {
        return this.downStation.equals(nextSection.upStation);
    }

    public boolean hasOverlappedStation(Section section) {
        return this.upStation.equals(section.upStation) || this.downStation.equals(section.downStation);
    }

    public boolean hasSameUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean hasSameDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public Stream<Station> getStations() {
        return Stream.of(upStation, downStation);
    }

    public long getUpStationId() {
        return upStation.getId();
    }

    public long getDownStationId() {
        return downStation.getId();
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, lineId);
    }
}
