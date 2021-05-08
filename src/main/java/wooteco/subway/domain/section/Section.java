package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;

public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;
    private Long lineId;

    public Section(Station upStation, Station downStation, int distance, Long lineId) {
        this(null, upStation, downStation, distance, lineId);
    }

    public Section(Long id, Station upStation, Station downStation, int distance, Long lineId) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Station getUpwardStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }
}
