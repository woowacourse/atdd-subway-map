package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private static final int MINIMUM_NUMBER_OF_STATION_IN_LINE = 2;

    private final List<Section> sections;

    public Sections(List<Section> sections){
        this.sections = sections;
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

    public final List<Long> sort(final Long upStationId){
        final Map<Long, Long> idTable = idTable();
        final List<Long> ids = new LinkedList<>();

        Long frontStationId = upStationId;
        while(idTable.containsKey(frontStationId)){
            ids.add(frontStationId);
            frontStationId = idTable.get(frontStationId);
        }
        ids.add(frontStationId);
        return ids;
    }

    private Map idTable(){
        final Map<Long, Long> idTable = new HashMap<>();
        for(final Section section : sections){
            idTable.put(section.frontStationId(), section.backStationId());
        }
        return idTable;
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

    private int numberOfStationInLine(){
        int numberOfSection = sections.size();
        if(numberOfSection == 0){
            return 0;
        }
        return numberOfSection+1;
    }

    public void validateAbleToDelete(final Long stationId) {
        if(!isIncludedStation(stationId)){
            throw new LineException("노선에 존재하지 않는 역을 제거할 수 없습니다.");
        }

        if(numberOfStationInLine() <= MINIMUM_NUMBER_OF_STATION_IN_LINE){
            throw new LineException("종점 뿐인 노선의 역을 제거할 수 없습니다.");
        }
    }
}
