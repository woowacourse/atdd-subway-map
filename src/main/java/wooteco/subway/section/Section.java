package wooteco.subway.section;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section() {
    }

    public Section(Long id, Section section) {
        this(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
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
