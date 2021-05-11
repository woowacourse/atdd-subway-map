package wooteco.subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<SimpleStation> sortSectionsByOrder() {
        Deque<Long> sortedStationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        init(sortedStationIds, upStationIds, downStationIds);
        sortByOrder(sortedStationIds, upStationIds, downStationIds);
        return wrapToSimpleStation(new ArrayList<>(sortedStationIds));
    }

    private void init(Deque<Long> sortedStationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        for (Section section : sections) {
            upStationIds.put(section.getUpStationId(), section.getDownStationId());
            downStationIds.put(section.getDownStationId(), section.getUpStationId());
        }

        Section randomSection = sections.get(0);
        sortedStationIds.addFirst(randomSection.getUpStationId());
        sortedStationIds.addLast(randomSection.getDownStationId());
    }

    private void sortByOrder(Deque<Long> sortedStationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        while (downStationIds.containsKey(sortedStationIds.peekFirst())) {
            final Long id = sortedStationIds.peekFirst();
            sortedStationIds.addFirst(downStationIds.get(id));
        }
        while (upStationIds.containsKey(sortedStationIds.peekLast())) {
            final Long id = sortedStationIds.peekLast();
            sortedStationIds.addLast(upStationIds.get(id));
        }
    }

    private List<SimpleStation> wrapToSimpleStation(List<Long> stationIds) {
        return stationIds.stream()
                .map(SimpleStation::new)
                .collect(Collectors.toList());
    }
}
