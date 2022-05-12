package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean canLinkWithUpStation(Section other) {
        return upStationId.equals(other.downStationId);
    }

    public boolean canLinkWithDownStation(Section other) {
        return downStationId.equals(other.upStationId);
    }

    public Boolean isSameUpStation(Section other) {
        return other.upStationId.equals(this.upStationId);
    }

    public Boolean isSameDownStation(Section other) {
        return other.downStationId.equals(this.downStationId);
    }

    public Boolean isLessThanDistance(Section other) {
        return other.distance < this.distance;
    }

    public Boolean hasUpStation(Long stationId) {
        return upStationId == stationId;
    }

    public Boolean hasDownStation(Long stationId) {
        return downStationId == stationId;
    }

    public Boolean hasStation(Long stationId) {
        return upStationId == stationId || downStationId == stationId;
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

    public List<Long> getStationId() {
        return new ArrayList<>(List.of(upStationId, downStationId));
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
