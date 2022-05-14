package wooteco.subway.domain2.section;

import java.util.LinkedList;
import java.util.List;
import wooteco.subway.domain2.station.Station;
import wooteco.subway.domain2.station.StationMap;

// TODO: names should be changed
public class SectionViews2 {

    private final SectionStationMap2 sectionMap;
    private final StationMap stationMap;

    private SectionViews2(SectionStationMap2 sectionMap,
                         StationMap stationMap) {
        this.sectionMap = sectionMap;
        this.stationMap = stationMap;
    }

    public static SectionViews2 of(List<Section> sections) {
        return new SectionViews2(
                SectionStationMap2.of(sections),
                StationMap.of(sections));
    }

    public List<Station> getSortedStationsList() {
        Long initialStationId = sectionMap.findAnyStationId();
        boolean isDownDirection = true;
        int stationEntityCount = stationMap.getSize();

        return toSortedStationList(initialStationId, isDownDirection, stationEntityCount);
    }

    private List<Station> toSortedStationList(Long initialStationId,
                                                    boolean isDownDirection,
                                                    int capacity) {
        LinkedList<Station> list = new LinkedList<>();
        list.add(stationMap.findEntityOfId(initialStationId));
        Long currentStationId = initialStationId;

        while (list.size() < capacity) {
            if (isDownDirection && sectionMap.hasDownStation(currentStationId)) {
                currentStationId = addDownStationIdAndGetNextKey(list, currentStationId);
                continue;
            }
            isDownDirection = false;
            currentStationId = addUpStationIdAndGetNextKey(list, currentStationId);
        }
        return list;
    }

    private Long addDownStationIdAndGetNextKey(List<Station> list, Long upStationId) {
        Long downStationId = sectionMap.getDownStationIdOf(upStationId);
        list.add(stationMap.findEntityOfId(downStationId));
        return downStationId;
    }

    private Long addUpStationIdAndGetNextKey(List<Station> list, Long downStationId) {
        Long upStationId = sectionMap.getUpStationIdOf(downStationId);
        list.add(0, stationMap.findEntityOfId(upStationId));
        return upStationId;
    }
}
