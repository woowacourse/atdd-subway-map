package wooteco.subway.domain.section;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public Section(Long lineId, Section section) {
        this(lineId,
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    public Section(Long upStationId, Long downStationId, Integer distance) {
        this(null, upStationId, downStationId, distance);
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Integer getDistance() {
        return distance;
    }
}
