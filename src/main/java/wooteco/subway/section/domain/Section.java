package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
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

    public Section(final Long frontStationId, final Long backStationId, final int distance) {
        this(null, null, frontStationId, backStationId, distance);
    }

    // TODO :: 변수명 질문하기
    public static Section combine(final Section section1, final Section section2) {
        if(section1.isBackStationId(section2.frontStationId)) {
            final int distance = section1.distance + section2.distance;
            return new Section(section1.lineId, section1.frontStationId, section2.backStationId, distance);
        }

        if(section2.isBackStationId(section1.frontStationId)) {
            final int distance = section1.distance + section2.distance;
            return new Section(section2.lineId, section2.frontStationId, section1.backStationId, distance);
        }

        throw new LineException("두 구간에 중복되는 역이 없습니다.");
    }

    public boolean isSameFrontStation(final Section section) {
        return frontStationId.equals(section.frontStationId);
    }

    public boolean isSameBackStation(final Section section) {
        return backStationId.equals(section.backStationId);
    }

    public boolean isFrontStationId(final Long stationId) {
        return frontStationId.equals(stationId);
    }

    public boolean isBackStationId(final Long stationId) {
        return backStationId.equals(stationId);
    }

    // TODO :: Sections와 합칠 수 있지 않을까
    public List<Section> devide(final Section section) {
        final List<Section> sections = Arrays.asList(section);

        if (isSameFrontStation(section)) {
            sections.add(new Section(lineId, section.backStationId(), backStationId, distance - section.distance()));
            return sections;
        }

        if (isSameBackStation(section)) {
            sections.add(new Section(lineId, frontStationId, section.frontStationId(), distance - section.distance()));
            return sections;
        }

        throw new LineException("올바른 구간이 아닙니다.");
    }

    public boolean isIncludeStation(final Long stationId){
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
