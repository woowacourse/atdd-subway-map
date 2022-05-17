package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(final Long id, final Long lineId, final Station upStation, final Station downStation, int distance) {
        validateDuplicateStation(upStation.getId(), downStation.getId());
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDuplicateStation(final Long upStationId, final Long downStationId) {
        if (Objects.equals(upStationId, downStationId)) {
            throw new IllegalStateException("상행 종점과 하행 종점은 같은 역이 될 수 없습니다.");
        }
    }

    public Section(final Long lineId, final Station upStation, final Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Long getId() {
        return id;
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
