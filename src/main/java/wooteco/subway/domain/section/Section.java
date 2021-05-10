package wooteco.subway.domain.section;

import java.util.Objects;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.type.Direction;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.HttpException;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validate(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, null, upStation.getId(), downStation.getId(), distance);
    }

    public Section(Line line, Long upStationId, Long downStationId, Integer distance) {
        this(null, line.getId(), upStationId, downStationId, distance);
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Section section) {
        this(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation.getId(), downStation.getId(), distance);
    }

    private void validate(int distance) {
        if (distance <= 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "구간의 길이는 1보다 크거나 같아야 합니다.");
        }
    }

    public Section getNewSplitSectionBy(Section newSection, Long newStationId, Direction directionOfInsertCriteriaStationInNewSection) {
        Direction reversedDirectionOfInsertCriteriaStation = directionOfInsertCriteriaStationInNewSection.getReversed();
        int splitDistance = this.getSplitDistanceBy(newSection);
        if (reversedDirectionOfInsertCriteriaStation == Direction.UP) {
            return new Section(lineId, this.getUpStationId(), newStationId, splitDistance);
        }
        return new Section(lineId, newStationId, this.getDownStationId(), splitDistance);
    }

    public Direction getDirectionOf(Long stationId) {
        if (stationId.equals(upStationId)) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    public boolean canBeSplitBy(Section newSection) {
        return newSection.getDistance() < this.getDistance();
    }

    public int getSplitDistanceBy(Section newSection) {
        return this.getDistance() - newSection.getDistance();
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
