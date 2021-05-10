package wooteco.subway.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public boolean isBothEnd(final Section section) {
        Deque<Long> ids = sortedStationIds();

        return Objects.equals(ids.peekFirst(), section.getDownStationId())
            || Objects.equals(ids.peekLast(), section.getUpStationId());
    }

    public ArrayDeque<Long> sortedStationIds() {
        Deque<Long> stationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        initStationIds(stationIds, upStationIds, downStationIds);
        sortStationsById(stationIds, upStationIds, downStationIds);

        return new ArrayDeque<>(stationIds);
    }

    private void initStationIds(Deque<Long> stationIds, Map<Long, Long> upStationIds,
        Map<Long, Long> downStationIds) {
        for (Section section : sections) {
            upStationIds.put(section.getUpStationId(), section.getDownStationId());
            downStationIds.put(section.getDownStationId(), section.getUpStationId());
        }

        Section section = sections.get(0);
        stationIds.addFirst(section.getUpStationId());
        stationIds.addFirst(section.getDownStationId());
    }

    private void sortStationsById(Deque<Long> stationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        while (upStationIds.containsKey(stationIds.peekLast())) {
            Long id = stationIds.peekLast();
            stationIds.addLast(upStationIds.get(id));
        }

        while (downStationIds.containsKey(stationIds.peekFirst())) {
            Long id = stationIds.peekFirst();
            stationIds.addFirst(downStationIds.get(id));
        }
    }
}
