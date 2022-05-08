package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, int distance) {
        Objects.requireNonNull(upStation);
        Objects.requireNonNull(downStation);
        validateDistanceOverZero(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistanceOverZero(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0 이하가 될 수 없습니다.");
        }
    }
}
