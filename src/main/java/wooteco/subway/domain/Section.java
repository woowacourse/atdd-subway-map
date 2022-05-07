package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id, final long lineId, final Station upStation, final Station downStation,
                   final int distance) {
        validatePositiveDistance(distance);
        validateDuplicateStation(upStation, downStation);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validatePositiveDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이는 양수만 들어올 수 있습니다.");
        }
    }

    private void validateDuplicateStation(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("upstation과 downstation은 중복될 수 없습니다.");
        }
    }

    public Section(final long lineId, final Station upStation, final Station downStation, final int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(final Long id, final Section section) {
        this(id, section.lineId, section.upStation, section.downStation, section.distance);
    }

    public boolean isSameUpStationAndDownStation(final Station upStation, final Station downStation) {
        return (this.upStation.equals(upStation) && this.downStation.equals(downStation)) ||
                (this.upStation.equals(downStation) && this.downStation.equals(upStation));
    }

    public boolean isUpSection(final Section section) {
        return this.upStation.equals(section.downStation);
    }

    public boolean isDownSection(final Section section) {
        return this.downStation.equals(section.upStation);
    }

    public boolean isUpSectionOrDownSection(final Section section) {
        return containsStation(section.upStation) || containsStation(section.downStation);
    }

    private boolean containsStation(final Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public boolean equalsUpStation(final Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean equalsDownStation(final Section section) {
        return downStation.equals(section.downStation);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
