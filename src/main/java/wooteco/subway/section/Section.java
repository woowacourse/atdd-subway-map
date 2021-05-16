package wooteco.subway.section;

import wooteco.subway.station.Station;

public class Section {

    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void checkInsertionPossible(Section section) {
        if (this.distance <= section.distance) {
            throw new IllegalArgumentException("새로운 구간의 거리는 기존 구간의 거리보다 작아야 합니다");
        }
    }

    public int addDistance(Section section) {
        return this.distance + section.distance;
    }

    public int subtractDistance(Section section) {
        return this.distance - section.distance;
    }

    public Long getLineId() {
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
