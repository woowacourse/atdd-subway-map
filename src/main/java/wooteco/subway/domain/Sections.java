package wooteco.subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getDistinctStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Long> getLastStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(it -> it))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Optional<Section> getExistedUpStationSection(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst();
    }

    public Optional<Section> getExistedDownStationSection(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst();
    }

    public boolean isLastStation(Long stationId) {
        return getLastStationIds().contains(stationId);
    }
}
