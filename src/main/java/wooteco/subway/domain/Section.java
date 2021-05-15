package wooteco.subway.domain;

import wooteco.subway.exception.DuplicateException;

public class Section {

    private final Id id;
    private final Id lineId;
    private final Id upStationId;
    private final Id downStationId;
    private final Distance distance;

    public Section(Long key, Section section) {
        this(new Id(key), section.lineId, section.upStationId, section.downStationId,
            section.distance);
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, new Id(lineId), new Id(upStationId), new Id(downStationId),
            new Distance(distance));
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(new Id(id), new Id(lineId), new Id(upStationId), new Id(downStationId),
            new Distance(distance));
    }

    public Section(Id id, Id lineId, Id upStatinoId, Id downStationId, Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStatinoId;
        this.downStationId = downStationId;
        this.distance = distance;
        validateDuplicateStations(this.upStationId, this.downStationId);
    }

    private void validateDuplicateStations(Id upStationId, Id downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new DuplicateException();
        }
    }

    public Section updateForSave(Section section) {
        Distance updateDistance = this.distance.subtract(section.distance);

        if (upStationId.equals(section.upStationId)) {
            return new Section(null, lineId, section.downStationId, downStationId, updateDistance);
        }
        return new Section(null, lineId, upStationId, section.upStationId, updateDistance);
    }

    public Section updateForDelete(Section section) {
        Distance updateDistance = section.distance.add(this.distance);
        return new Section(null, lineId, upStationId, section.downStationId, updateDistance);
    }

    public boolean hasSameStationBySection(Section section) {
        return hasSameStation(section.getUpStationId()) ||
            hasSameStation(section.getDownStationId());
    }

    public boolean hasSameStation(Long stationId) {
        return getUpStationId().equals(stationId) || getDownStationId().equals(stationId);
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

}
