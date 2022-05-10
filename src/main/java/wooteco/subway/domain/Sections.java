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

    public List<Long> getUpStationIds() {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    public Optional<Section> getExistedSection(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst();
    }
}
