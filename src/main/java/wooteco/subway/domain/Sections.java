package wooteco.subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public LinkedList<Long> getSortedStationIds() {
        LinkedList<Long> sortedIds = new LinkedList<>();

        Section target = sections.get(0);

        addUpStream(sortedIds, target.getUpStationId());
        sortedIds.add(target.getUpStationId());
        sortedIds.add(target.getDownStationId());
        addDownStream(sortedIds, target.getDownStationId());

        return sortedIds;
    }

    public boolean isTerminus(Section section) {
        LinkedList<Long> sortedStations = getSortedStationIds();
        return (section.isSameDownStation(sortedStations.getFirst()) ||
                section.isSameUpStation(sortedStations.getLast())
        );
    }

    private void addUpStream(LinkedList<Long> result, Long key) {
        Map<Long, Long> ids = sections.stream()
                .collect(Collectors.toMap(
                        Section::getDownStationId,
                        Section::getUpStationId
                ));

        while (ids.containsKey(key)) {
            key = ids.get(key);
            result.addFirst(key);
        }
    }

    private void addDownStream(LinkedList<Long> result, Long key) {
        Map<Long, Long> ids = sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStationId,
                        Section::getDownStationId
                ));

        while (ids.containsKey(key)) {
            key = ids.get(key);
            result.addLast(key);
        }
    }


}
