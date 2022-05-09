package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final Long lineId;

    public Section(final Long id, final Station upStation, final Station downStation, final int distance, final Long lineId) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(final Station upStation, final Station downStation, final int distance, final Long lineId) {
        this(null, upStation, downStation, distance, lineId);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isGreaterOrEqualTo(final Section other) {
        return this.distance >= other.distance;
    }
}
