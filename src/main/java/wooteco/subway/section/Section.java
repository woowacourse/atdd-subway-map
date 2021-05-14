package wooteco.subway.section;

import wooteco.subway.domain.Id;
import wooteco.subway.exception.section.DuplicateStationException;

public class Section {

    private final Id id;
    private final Id lineId;
    private final Id upStationId;
    private final Id downStationId;
    private final Distance distance;

    public Section(final long key, final Section section) {
        this(new Id(key), section.lineId, section.upStationId, section.downStationId,
            section.distance);
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId,
        final int distance) {

        this(null, new Id(lineId), new Id(upStationId), new Id(downStationId),
            new Distance(distance));
    }

    public Section(final Long id, final Long lineId, final Long upStationId,
        final Long downStationId, final int distance) {

        this(new Id(id), new Id(lineId), new Id(upStationId), new Id(downStationId),
            new Distance(distance));
    }

    public Section(final Id id, final Id lineId, final Id upStatinoId, final Id downStationId,
        final Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStatinoId;
        this.downStationId = downStationId;
        this.distance = distance;
        validateDuplicateStations(this.upStationId, this.downStationId);
    }

    private void validateDuplicateStations(final Id upStationId, final Id downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new DuplicateStationException();
        }
    }

    public Long getId() {
        return id.getValue();
    }

    public Long getLineId() {
        return lineId.getValue();
    }

    public Long getUpStationId() {
        return upStationId.getValue();
    }

    public Long getDownStationId() {
        return downStationId.getValue();
    }

    public int getDistance() {
        return distance.getValue();
    }

    public Section dividedSectionForSave(final Section section) {
        Distance updateDistance = this.distance.subtract(section.distance);

        if (upStationId.equals(section.upStationId)) {
            return new Section(null, lineId, section.downStationId, downStationId, updateDistance);
        }
        return new Section(null, lineId, upStationId, section.upStationId, updateDistance);
    }

    public Section combinedSectinoForDelete(final Section section) {
        Distance updateDistance = section.distance.add(this.distance);
        return new Section(null, lineId, upStationId, section.downStationId, updateDistance);
    }
}
