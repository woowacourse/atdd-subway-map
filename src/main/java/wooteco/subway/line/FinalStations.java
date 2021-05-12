package wooteco.subway.line;

public class FinalStations {
    private final Long upStationId;
    private final Long downStationId;

    public FinalStations(final Long upStationId, final Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public FinalStations addStations(final Long frontStationId, final Long backStationId) {
        if (this.upStationId.equals(backStationId)) {
            return new FinalStations(frontStationId, this.downStationId);
        }

        if (this.downStationId.equals(frontStationId)) {
            return new FinalStations(this.upStationId, backStationId);
        }

        return this;
    }

    public boolean isFinalSection(final Long frontStationId, final Long backStationId) {
        return this.upStationId.equals(backStationId) != this.downStationId.equals(frontStationId);
    }

    public Long upStationId() {
        return upStationId;
    }

    public Long downStationId() {
        return downStationId;
    }
}
