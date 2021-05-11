package wooteco.subway.line;

public class FinalStations {
    private final Long upStationId;
    private final Long downStationId;

    public FinalStations(Long upStationId, Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public FinalStations addStations(Long upStationId, Long downStationId) {
        if (this.upStationId.equals(downStationId)) {
            return new FinalStations(upStationId, this.downStationId);
        }

        if (this.downStationId.equals(upStationId)) {
            return new FinalStations(this.upStationId, downStationId);
        }

        return this;
    }

    public boolean isFinalSection(Long upStationId, Long downStationId) {
        return this.upStationId.equals(downStationId) != this.downStationId.equals(upStationId);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
