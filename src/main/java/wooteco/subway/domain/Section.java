package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.utils.exception.SectionCreateException;

public class Section {

    private static final int DISTANCE_STANDARD = 0;
    private static final String DISTANCE_FAIL_MESSAGE = "거리는 0km 초과이어야 합니다.";

    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(final Long id,
                   final Long lineId,
                   final Station upStation,
                   final Station downStation,
                   final int distance
    ) {
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    private void validateDistance(final int distance) {
        if (distance <= DISTANCE_STANDARD) {
            throw new SectionCreateException(DISTANCE_FAIL_MESSAGE);
        }
    }

    public boolean haveStation(final Station upStation, final Station downStation) {
        return isSameUpStation(upStation, downStation) || isSameDownStation(upStation, downStation);

    }

    private boolean isSameUpStation(final Station upStation, final Station downStation) {
        return this.upStation.equals(upStation) || this.upStation.equals(downStation);
    }

    private boolean isSameDownStation(final Station upStation, final Station downStation) {
        return this.downStation.equals(upStation) || this.downStation.equals(downStation);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation);
    }
}
