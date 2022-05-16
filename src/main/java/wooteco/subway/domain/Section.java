package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.utils.exception.SectionCreateException;

public class Section {

    private static final int DISTANCE_STANDARD = 0;
    private static final String NULL_PREVENT_MESSAGE = "[ERROR] 필수 입력 사항을 입력해주세요. cause : ";
    private static final String DISTANCE_FAIL_MESSAGE = "거리는 0km 초과이어야 합니다.";

    private final Long id;
    private final Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(final Long id,
                   final Long lineId,
                   final Station upStation,
                   final Station downStation,
                   final int distance) {
        validateDistance(distance);
        validateSameSection(upStation, downStation);
        this.id = id;
        this.lineId = Objects.requireNonNull(lineId, NULL_PREVENT_MESSAGE + "lineId");
        this.upStation = Objects.requireNonNull(upStation, NULL_PREVENT_MESSAGE + "upStation");
        this.downStation = Objects.requireNonNull(downStation, NULL_PREVENT_MESSAGE + "downStation");
        this.distance = distance;
    }

    public Section(final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    private void validateSameSection(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new SectionCreateException("동일한 역은 구간으로 등록할 수 없습니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= DISTANCE_STANDARD) {
            throw new SectionCreateException(DISTANCE_FAIL_MESSAGE);
        }
    }

    public boolean haveStation(final Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        return isSameUpStation(upStation)
                || isSameUpStation(downStation)
                || isSameDownStation(upStation)
                || isSameDownStation(downStation);
    }

    public boolean isSameUpStation(final Station station) {
        return upStation.equals(station);
    }

    public boolean isSameDownStation(final Station station) {
        return downStation.equals(station);
    }

    public void updateStations(final Station upStation, final Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public boolean isLongerThan(final int distance) {
        return this.distance > distance;
    }

    public void subtractDistance(final int distance) {
        this.distance -= distance;
    }

    public boolean isUpdate(final Section section) {
        return equals(section) && !isSameStations(section);
    }

    private boolean isSameStations(final Section section) {
        return isSameUpStation(section.getUpStation()) && isSameDownStation(section.getDownStation());
    }

    public Section merge(final Section section) {
        int sumDistance = distance + section.distance;
        if (isSameDownStation(section.upStation)) {
            return new Section(lineId, upStation, section.downStation, sumDistance);
        }
        return new Section(lineId, section.downStation, upStation, sumDistance);
    }

    public boolean isSameSection(final Section section) {
        return equals(section) && isSameStations(section);
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
