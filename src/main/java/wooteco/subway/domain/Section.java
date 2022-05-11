package wooteco.subway.domain;

public class Section {

    private final Long upStationId;
    private final Long downStationId;

    public Section(Long upStationId, Long downStationId) {
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
        return "Section{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                '}';
    }
}
