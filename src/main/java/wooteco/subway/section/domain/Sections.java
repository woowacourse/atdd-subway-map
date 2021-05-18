package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sections {
    private static final int MINIMUM_NUMBER_OF_STATION_IN_LINE = 2;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public Section findSectionInclude(final Section section) {
        return sections.stream()
                .filter(sectionInLine -> sectionInLine.isBackStationId(section.backStationId())
                        || sectionInLine.isFrontStationId(section.frontStationId()))
                .findFirst()
                .orElseThrow(() -> new LineException("해당 구간을 찾을 수 없습니다."));
    }

    public Section finalSectionInclude(final Long stationId) {
        return sections.stream()
                .filter(section -> section.isIncludedStation(stationId))
                .findFirst()
                .orElseThrow(() -> new LineException("존재하지 않는 역입니다."));
    }

    public final List<Long> sort(final Long upStationId) {
        final Map<Long, Long> idTable = idTable();
        final List<Long> ids = new LinkedList<>();

        Long frontStationId = upStationId;
        while (idTable.containsKey(frontStationId)) {
            ids.add(frontStationId);
            frontStationId = idTable.get(frontStationId);
        }
        ids.add(frontStationId);

        return ids;
    }

    private Map idTable() {
        final Map<Long, Long> idTable = new HashMap<>();
        for (final Section section : sections) {
            idTable.put(section.frontStationId(), section.backStationId());
        }
        return idTable;
    }

    private boolean isIncludedStation(final Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.isIncludedStation(stationId));
    }

    public int numberOfStationInLine() {
        int numberOfSection = sections.size();
        if (numberOfSection == 0) {
            return 0;
        }
        return numberOfSection + 1;
    }

    public void validateAbleToAdd(final Section section) {
        final boolean isFrontStationIncluded = isIncludedStation(section.frontStationId());
        final boolean isBackStationIncluded = isIncludedStation(section.backStationId());

        if (isFrontStationIncluded == isBackStationIncluded) {
            throw new LineException("하나의 역이 포함되어있어야 합니다.");
        }
    }

    public void validateAbleToDelete(final Long stationId) {
        if (!isIncludedStation(stationId)) {
            throw new LineException("노선에 존재하지 않는 역을 제거할 수 없습니다.");
        }

        if (numberOfStationInLine() <= MINIMUM_NUMBER_OF_STATION_IN_LINE) {
            throw new LineException("종점 뿐인 노선의 역을 제거할 수 없습니다.");
        }
    }

    public Section findSectionByFrontStation(final Long stationId) {
        return sections.stream()
                .filter(section -> section.isFrontStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new LineException("존재하지 않는 구간입니다."));
    }

    public Section findSectionByBackStation(final Long stationId) {
        return sections.stream()
                .filter(section -> section.isBackStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new LineException("존재하지 않는 구간입니다."));
    }
}
