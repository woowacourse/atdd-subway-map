package wooteco.subway.domain;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public boolean existsStation(Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    public boolean isSameUpStation(Section section) {
        return upStationId.equals(section.upStationId);
    }

    public boolean isSameDownStation(Section section) {
        return downStationId.equals(section.downStationId);
    }

    public boolean isSameUpAndDownStation(Section section) {
        return upStationId.equals(section.upStationId) && downStationId.equals(section.downStationId);
    }

    public void changeUpStation(Section section) {
        this.upStationId = section.downStationId;
        this.distance -= section.distance;
    }

    public void changeDownStation(Section section) {
        this.downStationId = section.upStationId;
        this.distance -= section.distance;
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
