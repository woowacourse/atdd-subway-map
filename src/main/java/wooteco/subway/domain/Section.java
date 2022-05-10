package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.SectionDistanceExceedException;
import wooteco.subway.exception.SubwayUnknownException;

public class Section {

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

    public boolean isSameLineId(Long lineId) {
        return Objects.equals(this.lineId, lineId);
    }

    public boolean isSameDownStation(Section other) {
        return Objects.equals(this.downStation, other.downStation);
    }

    public boolean isSameUpStation(Section other) {
        return Objects.equals(this.upStation, other.upStation);
    }

    public int countMatchedStations(Section other) {
        int count = 0;

        if (hasSingleJoinPoint(other)) {
            return 1;
        }
        if (isSameUpStation(other)) {
            count++;
        }
        if (isSameDownStation(other)) {
            count++;
        }

        return count;
    }

    private boolean hasSingleJoinPoint(Section other) {
        return (this.isSameUpStation(other) && !this.isSameDownStation(other))
                || (!this.isSameUpStation(other) && this.isSameDownStation(other))
                || Objects.equals(this.upStation, other.downStation)
                || Objects.equals(this.downStation, other.upStation);

    }

    public boolean isJoinable(Section other) {
        return this.distance > other.distance;
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

    public SectionResult sync(Section input) {
        // 상행 확장
        if (this.upStation == input.downStation) {
            return SectionResult.UP_EXTENDED;
        }

        // 하행 확장
        if (this.downStation == input.upStation) {
            return SectionResult.DOWN_EXTENDED;
        }

        // 거리 초과 검증
        if (!isWider(input)) {
            throw new SectionDistanceExceedException(input.getDistance());
        }

        // 상행역 기준으로 가운데 역 추가
        if (this.isSameUpStation(input)) {
            this.upStation = input.downStation;
            this.distance = this.distance - input.distance;
            return SectionResult.MIDDLE_ADDED;
        }

        // 하행역 기준 가운데 역 추가
        if (this.isSameDownStation(input)) {
            this.downStation = input.upStation;
            this.distance = this.distance - input.distance;
            return SectionResult.MIDDLE_ADDED;
        }

        throw new SubwayUnknownException("구간 확장 처리 중 예외가 발생했습니다");
    }

    public boolean isWider(Section input) {
        return this.distance > input.distance;
    }
}
