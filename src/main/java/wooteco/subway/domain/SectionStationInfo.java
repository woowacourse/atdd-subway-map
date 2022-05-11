package wooteco.subway.domain;

public class SectionStationInfo {

    private final Long upStationId;
    private final Long downStationId;

    public SectionStationInfo(Long upStationId, Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    @Override
    public String toString() {
        return "Stations{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                '}';
    }
}
