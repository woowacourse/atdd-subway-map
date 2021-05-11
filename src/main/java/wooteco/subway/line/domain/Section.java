package wooteco.subway.line.domain;

public class Section {
    private final Long upStationId;
    private final Long downStationId;

    public Section(final Long upStationId, final Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
