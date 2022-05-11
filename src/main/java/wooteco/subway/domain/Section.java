package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.SectionDistanceExceedException;
import wooteco.subway.exception.SubwayUnknownException;

public class Section implements Comparable<Section> {

    private final Long id;
    private final Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, null, upStation, downStation, distance);
    }

    public boolean isSameLineId(Long lineId) {
        return Objects.equals(this.lineId, lineId);
    }

    public boolean hasSameDownStation(Section other) {
        return Objects.equals(this.downStation, other.downStation);
    }

    public boolean hasSameUpStation(Section other) {
        return Objects.equals(this.upStation, other.upStation);
    }

    public boolean isAddable(Section other) {
        return hasSameUpStation(other) || hasSameDownStation(other);
    }

    public SectionResult sync(Section input) {
        // 상행 확장
        if (Objects.equals(this.upStation, input.downStation)) {
            return SectionResult.UP_EXTENDED;
        }

        // 하행 확장
        if (Objects.equals(this.downStation, input.upStation)) {
            return SectionResult.DOWN_EXTENDED;
        }

        // 거리 초과 검증
        if (!isWider(input)) {
            throw new SectionDistanceExceedException(input.getDistance());
        }

        // 상행역 기준으로 가운데 역 추가
        if (this.hasSameUpStation(input)) {
            this.upStation = input.downStation;
            this.distance = this.distance - input.distance;
            return SectionResult.MIDDLE_ADDED;
        }

        // 하행역 기준 가운데 역 추가
        if (this.hasSameDownStation(input)) {
            this.downStation = input.upStation;
            this.distance = this.distance - input.distance;
            return SectionResult.MIDDLE_ADDED;
        }

        throw new SubwayUnknownException("구간 확장 처리 중 예외가 발생했습니다");
    }

    public boolean isWider(Section input) {
        return this.distance > input.distance;
    }

    public boolean hasStationById(Long stationId) {
        return upStation.isSameId(stationId) || downStation.isSameId(stationId);
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
    public int compareTo(Section o) {
        if (Objects.equals(this.downStation, o.upStation)) {
            return -1;
        }
        if (Objects.equals(this.upStation, o.downStation)) {
            return 1;
        }
        return 0;
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
