package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

public class Section {

    private Long id;
    private final int distance;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;

    public Section(int distance, Long lineId, Long upStationId, Long downStationId) {
        this.distance = distance;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long id, int distance, Long lineId, Long upStationId, Long downStationId) {
        this(distance, lineId, upStationId, downStationId);
        this.id = id;
    }

    public static Section of(Line line, LineRequest lineRequest) {
        return new Section(
                lineRequest.getDistance(),
                line.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId()
        );
    }

    public static Section of(Line line, SectionRequest sectionRequest) {
        return new Section(
                sectionRequest.getDistance(),
                line.getId(),
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId()
        );
    }

    public Section createSection(Section sectionToInsert) {
        int generatedDistance = distance - sectionToInsert.getDistance();
        if (isUpStationIdEquals(sectionToInsert)) {
            return new Section(id, generatedDistance, lineId, sectionToInsert.downStationId, downStationId);
        }
        return new Section(id, generatedDistance, lineId, upStationId, sectionToInsert.upStationId);
    }

    public boolean canAddAsLastStation(Long upLastStationId, Long downLastStationId) {
        return upLastStationId.equals(downStationId) != downLastStationId.equals(upStationId) ;
    }

    public boolean isEqualDownStationId(Long stationId) {
        return downStationId.equals(stationId);
    }

    public boolean isEqualUpStationId(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean isUpStationIdEquals(Section section) {
        return section.upStationId.equals(this.upStationId);
    }

    public boolean isDownStationIdEquals(Section section) {
        return section.downStationId.equals(this.downStationId);
    }

    public boolean isDistanceBiggerThan(Section section) {
        return distance >= section.getDistance();
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
