package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.Deque;
import wooteco.subway.exception.SameStationIdException;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private Section() {
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validateDifferentStationIds(upStationId, downStationId);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateDifferentStationIds(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SameStationIdException();
        }
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

    public boolean isUpStation(Long id) {
        return id.equals(upStationId);
    }

    public boolean isDownStation(Long id) {
        return id.equals(downStationId);
    }

    public boolean isEndPointOf(Sections sections) {
        Deque<Long> ids = new ArrayDeque<>(sections.sortedStationIds());
        return ids.peekFirst().equals(downStationId) || ids.peekLast().equals(upStationId);
    }

    public boolean largerThan(int existingDistance) {
        return distance >= existingDistance;
    }

    public int deductDistance(Section newSection) {
        return distance - newSection.getDistance();
    }
}
