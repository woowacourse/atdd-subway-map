package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;

public class Section2 {

    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section2(Long lineId, Station upStation, Station downStation, int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isRegisteredAtLine(Long lineId) {
        return lineId.equals(this.lineId);
    }

    public Section toDomain() {
        return new Section(upStation, downStation, distance);
    }
}
