package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;

import java.util.LinkedList;
import java.util.List;

public class Section {
    private Long id;
    private Long lineId;
    private Long frontStationId;
    private Long backStationId;
    private int distance;

    public Section(final Long id, final Long lineId, final Long frontStationId, final Long backStationId, final int distance) {
        if (distance <= 0) {
            throw new LineException("거리는 음수일 수 없습니다.");
        }

        this.id = id;
        this.lineId = lineId;
        this.frontStationId = frontStationId;
        this.backStationId = backStationId;
        this.distance = distance;
    }

    public Section(final Long LineId, final Long frontStationId, final Long backStationId, final int distance) {
        this(null, LineId, frontStationId, backStationId, distance);
    }

    public Section combine(final Section followSection) {
        if (isBackStationId(followSection.frontStationId)) {
            final int sumDistance = this.distance + followSection.distance;
            return new Section(lineId, this.frontStationId, followSection.backStationId, sumDistance);
        }
        throw new LineException("두 구간은 합칠 수 있는 역이 아닙니다.");
    }

    public List<Section> divide(final Section section) {
        final List<Section> sections = new LinkedList<>();
        sections.add(section);

        if (isFrontStationId(section.frontStationId)) {
            sections.add(new Section(lineId, section.backStationId(), backStationId, distance - section.distance()));
            return sections;
        }

        if (isBackStationId(section.backStationId)) {
            sections.add(new Section(lineId, frontStationId, section.frontStationId(), distance - section.distance()));
            return sections;
        }

        throw new LineException("올바른 구간이 아닙니다.");
    }

    public boolean isFrontStationId(final Long stationId) {
        return frontStationId.equals(stationId);
    }

    public boolean isBackStationId(final Long stationId) {
        return backStationId.equals(stationId);
    }

    public boolean isIncludedStation(final Long stationId) {
        return frontStationId.equals(stationId) || backStationId.equals(stationId);
    }

    public Long frontStationId() {
        return frontStationId;
    }

    public Long backStationId() {
        return backStationId;
    }

    public int distance() {
        return distance;
    }

    public Long lineId() {
        return lineId;
    }

    public Long id() {
        return id;
    }
}
