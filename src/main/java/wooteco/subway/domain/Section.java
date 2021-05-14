package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    public Section() {

    }

    public Section(Long lineId, SimpleSection section) {
        this(lineId, section.getUpStationId(), section.getDownStationId(), new Distance(section.getDistance()));
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = new Distance(distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Distance distance) {
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

    public int getDistance() {
        return distance.getDistance();
    }

    public boolean isEqualUpStationId(SimpleSection section) {
        return upStationId.equals(section.getUpStationId());
    }

    public boolean isLongerDistanceThan(SimpleSection section) {
        return distance.isLongerDistanceThan(section);
    }

    public int calculateMaxDistance(SimpleSection simpleSection) {
        return distance.calculateMax(new Distance(simpleSection.getDistance()));
    }

    public int calculateMinDistance(SimpleSection simpleSection) {
        return distance.calculateMin(new Distance(simpleSection.getDistance()));
    }
}
