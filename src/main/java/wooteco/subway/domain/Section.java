package wooteco.subway.domain;

import wooteco.subway.exception.SubwayException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateSameStation(upStationId, downStationId);
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSameStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SubwayException("상행역과 하행역은 같을 수 없습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new SubwayException("구간의 길이는 양수여야 합니다.");
        }
    }

    public Section getUpdatedSectionForSameDownStation(Section newSection) {
        return new Section(id, lineId, upStationId,
                newSection.getUpStationId(), distance - newSection.getDistance());
    }

    public Section getUpdatedSectionForSameUpStation(Section newSection) {
        return new Section(id, lineId, newSection.getDownStationId(),
                downStationId, distance - newSection.getDistance());
    }

    public Section getUpdatedSectionForDelete(Section downStation) {
        return new Section(id, lineId, upStationId,
                downStation.getDownStationId(), distance + downStation.getDistance());
    }

    public boolean equalsUpOrDownStationId(Section section) {
        return equalsUpStationId(section.getUpStationId()) || equalsDownStationId(section.getDownStationId());
    }

    public boolean equalsUpStationId(Long otherUpStationId) {
        return this.upStationId.equals(otherUpStationId);
    }

    public boolean equalsDownStationId(Long otherDownStationId) {
        return this.downStationId.equals(otherDownStationId);
    }

    public boolean isShorterDistance(Section section) {
        return distance <= section.getDistance();
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
}
