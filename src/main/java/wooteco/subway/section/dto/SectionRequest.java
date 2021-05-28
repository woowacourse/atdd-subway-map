package wooteco.subway.section.dto;

import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.Objects;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public static SectionRequest from(LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public static SectionRequest of(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        return new SectionRequest(upStation.getId(), downStation.getId(), section.getDistance());
    }

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionRequest that = (SectionRequest) o;
        return distance == that.distance && Objects.equals(upStationId, that.upStationId) && Objects.equals(downStationId, that.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
