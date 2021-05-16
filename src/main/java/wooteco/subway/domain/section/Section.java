package wooteco.subway.domain.section;

import java.util.Objects;

import wooteco.subway.exception.illegal.ImpossibleDistanceException;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.section.SectionRequest;

public class Section {
    private final long upStationId;
    private final long downStationId;
    private final long lineId;
    private int distance;

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = validateDistance(distance);
        this.lineId = lineId;
    }

    public Section(long lineId, SectionRequest sectionRequest) {
        this(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    public Section(long id, LineRequest lineRequest) {
        this(id, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    private int validateDistance(int distance) {
        if (distance <= 0) {
            throw new ImpossibleDistanceException();
        }
        return distance;
    }

    public void updateValidDistance(Section newSection) {
        if (this.distance < newSection.distance) {
            throw new ImpossibleDistanceException();
        }
        this.distance -= newSection.distance;
    }

    public void addDistance(Section nextSection) {
        this.distance += nextSection.distance;
    }

    public boolean contains(long stationId) {
        return this.downStationId == stationId || this.upStationId == stationId;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return upStationId == section.upStationId && downStationId == section.downStationId && lineId == section.lineId
            && distance == section.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, lineId, distance);
    }
}
