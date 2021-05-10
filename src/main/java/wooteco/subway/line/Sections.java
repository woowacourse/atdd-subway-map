package wooteco.subway.line;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> stationRoute() {
        Map<Long, Section> upStationMap = new HashMap<>();
        Map<Long, Section> downStationMap = new HashMap<>();

        Deque<Long> sortedStationId = new ArrayDeque<>();

        for (Section section : sections) {
            upStationMap.put(section.getUpStationId(), section);
            downStationMap.put(section.getDownStationId(), section);
        }

        sortedStationId.addFirst(sections.get(0).getUpStationId());
        sortedStationId.addLast(sections.get(0).getDownStationId());

        while (upStationMap.containsKey(sortedStationId.getLast())) {
            sortedStationId.addLast(upStationMap.get(sortedStationId.getLast()).getDownStationId());
        }

        while (downStationMap.containsKey(sortedStationId.getFirst())) {
            sortedStationId.addFirst(downStationMap.get(sortedStationId.getFirst()).getUpStationId());
        }

        return new ArrayList<>(sortedStationId);
    }
}
