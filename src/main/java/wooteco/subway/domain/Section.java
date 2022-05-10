package wooteco.subway.domain;

import wooteco.subway.dto.section.SectionRequest;

public class Section {

    private static final String SAME_UP_DOWN = "상행종점과 하행종점은 같은 지하철역일 수 없습니다.";
    private static final String WRONG_DISTANCE = "거리는 1이상의 정수만 허용됩니다.";

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long lineId, SectionRequest sectionRequest) {
        validateSameId(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        validateDistance(sectionRequest.getDistance());
        return new Section(0L,
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );
    }

    public boolean isFront(Section section) {
        return this.downStationId == section.upStationId;
    }

    public boolean isBack(Section section) {
        return this.upStationId == section.downStationId;
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

    private static void validateSameId(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException(SAME_UP_DOWN);
        }
    }

    private static void validateDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException(WRONG_DISTANCE);
        }
    }

}
