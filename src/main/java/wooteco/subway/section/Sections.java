package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.exception.NoneOrAllStationsExistingInLineException;

public class Sections {

    private static final int MIDDLE_SECTION_CRITERIA = 2;
    private static final int VALID_SECTION_DUPLICATE_STATION_ID_CRITERIA = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isNotEndPoint() {
        return sections.size() == MIDDLE_SECTION_CRITERIA;
    }

    public Long findUpStationId(Long stationId) {
        return sections.stream()
            .filter(section -> stationId != section.getUpStationId())
            .findAny()
            .get()
            .getUpStationId();
    }

    public long findDownStationId(Long stationId) {
        return sections.stream()
            .filter(section -> stationId != section.getDownStationId())
            .findAny()
            .get()
            .getUpStationId();
    }

    public int sumDistance() {
        return sections.stream()
            .mapToInt(Section::getDistance)
            .sum();
    }

    public void validateSectionStations(Section newSection) {
        List<Long> stationIds = sections.stream()
            .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());

        List<Long> newSectionStationsId = Arrays.asList(newSection.getUpStationId(),
            newSection.getDownStationId());

        stationIds.retainAll(newSectionStationsId);
        if(stationIds.size() != VALID_SECTION_DUPLICATE_STATION_ID_CRITERIA){
            throw new NoneOrAllStationsExistingInLineException();
        }
    }

    public List<Long> sortedStationIds(){
        Deque<Long> sortedIds = new ArrayDeque<>();
        Map<Long, Long> upIds = new LinkedHashMap<>();
        Map<Long, Long> downIds = new LinkedHashMap<>();

        initializeByIds(sortedIds, upIds, downIds);
        sort(sortedIds, upIds, downIds);

        return new ArrayList<>(sortedIds);
    }

    private void initializeByIds(Deque<Long> sortedIds, Map<Long, Long> upIds,
        Map<Long, Long> downIds) {
        for (Section section : sections) {
            upIds.put(section.getDownStationId(), section.getUpStationId());
            downIds.put(section.getUpStationId(), section.getDownStationId());
        }

        Section now = sections.get(0);
        sortedIds.addFirst(now.getUpStationId());
    }

    private void sort(Deque<Long> sortedIds, Map<Long, Long> upIds, Map<Long, Long> downIds) {
        while(upIds.containsKey(sortedIds.peekFirst())){
            Long currentId = sortedIds.peekFirst();
            sortedIds.addFirst(upIds.get(currentId));
        }
        while (downIds.containsKey(sortedIds.peekLast())){
            Long currentId = sortedIds.peekLast();
            sortedIds.addLast(downIds.get(currentId));
        }
    }
}
