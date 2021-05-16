package wooteco.subway.domain.line.section;

import java.util.*;
import java.util.stream.LongStream;

public class OrderedStationId {

    private final List<Section> sections;

    public OrderedStationId(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> asList() {
        if (sections.isEmpty()) return Collections.emptyList();

        int maxStationsId = (int) getMaxStationsId();

        long[] stationIndexesByUpStationId = new long[maxStationsId + 1];
        long[] stationIndexesByDownStationId = new long[maxStationsId + 1];

        initializeStationIdArray(stationIndexesByUpStationId, stationIndexesByDownStationId);

        return sortStationIds(stationIndexesByUpStationId, stationIndexesByDownStationId);
    }

    private List<Long> sortStationIds(long[] stationIndexesByUpStationId,
                                      long[] stationIndexesByDownStationId) {
        Deque<Long> stationIds = new ArrayDeque<>();

        Section section = sections.get(0);
        stationIds.addFirst(section.getUpStationId());
        stationIds.addLast(section.getDownStationId());

        long nextId;
        while ((nextId = stationIndexesByDownStationId[stationIds.getFirst().intValue()]) != 0) {
            stationIds.addFirst(nextId);
        }

        while ((nextId = stationIndexesByUpStationId[stationIds.getLast().intValue()]) != 0) {
            stationIds.addLast(nextId);
        }

        return new ArrayList<>(stationIds);
    }

    private void initializeStationIdArray(long[] stationIndexesByUpStationId,
                                          long[] stationIndexesByDownStationId) {
        sections.forEach(section -> {
            int upStationId = section.getUpStationId().intValue();
            int downStationId = section.getDownStationId().intValue();

            stationIndexesByUpStationId[upStationId] = downStationId;
            stationIndexesByDownStationId[downStationId] = upStationId;
        });
    }

    private long getMaxStationsId() {
        return sections.stream()
                .flatMapToLong(section -> LongStream.of(
                        section.getUpStationId(),
                        section.getDownStationId()))
                .max()
                .orElse(-1L);
    }

}
