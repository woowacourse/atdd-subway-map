package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import wooteco.subway.exception.section.NoSuchSectionException;
import wooteco.subway.exception.station.NoSuchStationException;

public class Sections {

    private static final int END_STATION_COUNT = 1;

    private final List<Section> value;

    public Sections(final List<Section> value) {
        this.value = value;
    }

    public List<Long> toStations() {
        final List<Long> endStationIds = findEnsStationIds();
        Long upStationId = findEndUpStation(endStationIds);

        final List<Long> sortedStationIds = new ArrayList<>();
        sortedStationIds.add(upStationId);
        while (sortedStationIds.size() != value.size() + 1) {
            final Section section = findSectionByUpStationId(upStationId);
            upStationId = section.getDownStationId();
            sortedStationIds.add(upStationId);
        }
        return sortedStationIds;
    }

    private List<Long> findEnsStationIds() {
        return toCountByStationId()
                .entrySet()
                .stream()
                .filter(it -> it.getValue().equals(END_STATION_COUNT))
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> toCountByStationId() {
        final Map<Long, Integer> countByStationId = new HashMap<>();
        for (Section section : value) {
            final Long upStationId = section.getUpStationId();
            countByStationId.put(upStationId, countByStationId.getOrDefault(upStationId, 0) + 1);

            final Long downStationId = section.getDownStationId();
            countByStationId.put(downStationId, countByStationId.getOrDefault(downStationId, 0) + 1);
        }
        return countByStationId;
    }

    private Long findEndUpStation(final List<Long> endStationIds) {
        return value
                .stream()
                .map(Section::getUpStationId)
                .filter(endStationIds::contains)
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }

    private Section findSectionByUpStationId(final Long upStationId) {
        return value
                .stream()
                .filter(it -> it.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(NoSuchSectionException::new);
    }
}
