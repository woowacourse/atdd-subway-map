package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.SectionException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;


    public Section(Long id, Long lineId, Station upStation, Station downStation, Integer distance) {
        if (upStation.equals(downStation)) {
            throw new SectionException(ExceptionMessage.SAME_STATIONS_SECTION.getContent());
        }
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long lineId, Station upStation, Station downStation, Integer distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public boolean isForDivide(Section other) {
        return isDownDivide(other) || isUpDivide(other);
    }

    private boolean isDownDivide(Section other) {
        return downStation.equals(other.downStation);
    }

    private boolean isUpDivide(Section other) {
        return upStation.equals(other.upStation);
    }

    public Section divideFrom(Section other) {
        checkStationsNotSame(other);
        checkDistance(other);
        int distanceGap = distance - other.distance;
        if (isUpDivide(other)) {
            return new Section(id, lineId, other.downStation, downStation, distanceGap);
        }
        if (isDownDivide(other)) {
            return new Section(id, lineId, upStation, other.upStation, distanceGap);
        }
        throw new SectionException(ExceptionMessage.INVALID_DIVIDE_SECTION.getContent());
    }

    private void checkStationsNotSame(Section other) {
        if (upStation.equals(other.upStation) && downStation.equals(other.downStation)) {
            throw new SectionException(ExceptionMessage.SAME_STATIONS_SECTION.getContent());
        }
    }

    private void checkDistance(Section other) {
        if (distance <= other.distance) {
            throw new SectionException(ExceptionMessage.INVALID_INSERT_SECTION_DISTANCE.getContent());
        }
    }

    public boolean hasStation(Station station) {
        return this.downStation.equals(station) || upStation.equals(station);
    }

    public Section merge(Section other) {
        checkStationsNotSame(other);
        int mergedDistance = distance + other.distance;

        if (upStation.equals(other.downStation)) {
            return new Section(id, lineId, other.upStation, downStation, mergedDistance);
        }
        if (downStation.equals(other.upStation)) {
            return new Section(id, lineId, upStation, other.downStation, mergedDistance);
        }
        throw new SectionException(ExceptionMessage.NOT_CONNECTED_SECTIONS.getContent());
    }

    public boolean isUpperThan(Section other) {
        return downStation.equals(other.upStation);
    }

    public boolean isDownerThan(Section other) {
        return upStation.equals(other.downStation);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
