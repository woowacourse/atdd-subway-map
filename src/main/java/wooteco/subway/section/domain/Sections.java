package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections){
        this.sections = sections;
    }

    public void validateToAdd(final Section section) {
        // TODO :: 둘 다 이미 존재하거나, 둘 다 없는 경우
    }

    public Section findSectionInclude(final Section section) {
        return sections.stream()
                .filter(sectionInLine -> sectionInLine.isSameBackStation(section)
                        || sectionInLine.isSameFrontStation(section))
                .findFirst()
                .orElseThrow(()-> new LineException("해당 구간을 찾을 수 없습니다."));
    }

    public List<Section> sectionsIncludeStation(final Long stationId) {
        return sections.stream()
                .filter(section -> section.isIncludedStation(stationId))
                .collect(Collectors.toList());
    }

    public Order sort(final Long upStationId){
        final Map<Long, Long> idTable = new HashMap<>();
        for(final Section section : sections){
            idTable.put(section.frontStationId(), section.backStationId());
        }

        final List<Long> ids = new LinkedList<>();

        Long frontStationId = upStationId;
        while(idTable.containsValue(frontStationId)){
            ids.add(frontStationId);
            frontStationId = idTable.get(upStationId);
        }
        ids.add(frontStationId);

        return new Order(ids);
    }

    public void validateAbleToAdd(final Section section) {
        final boolean isFrontStationIncluded = isIncludedStation(section.frontStationId());
        final boolean isBackStationIncluded = isIncludedStation(section.backStationId());

        if(isFrontStationIncluded == isBackStationIncluded){
            throw new LineException("하나의 역이 포함되어있어야 합니다.");
        }
    }

    private boolean isIncludedStation(final Long stationId){
        return sections.stream()
                .anyMatch(section -> section.isIncludedStation(stationId));
    }
}
