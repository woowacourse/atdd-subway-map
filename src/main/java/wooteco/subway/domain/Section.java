package wooteco.subway.domain;

public class Section {

    private final long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(long id, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(final int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리가 1 미만인 구간 정보는 생성할 수 없습니다.");
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
