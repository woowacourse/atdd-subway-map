package wooteco.subway.domain;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private final Long id;
    private final long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id, final long lineId, final Station upStation, final Station downStation,
            final int distance) {
        validateDistance(distance);
        validateDuplicateStation(upStation, downStation);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(final int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("두 종점간의 거리는 0보다 커야합니다.");
        }
    }

    private void validateDuplicateStation(final Station upStation, final Station downStation) {
        if (upStation.isSameStation(downStation)) {
            throw new IllegalArgumentException("두 종점은 같을 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
