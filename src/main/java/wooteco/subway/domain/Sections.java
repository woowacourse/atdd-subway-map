package wooteco.subway.domain;

import wooteco.subway.service.dto.SimpleStation;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> sortBySectionId() {
        return Collections.unmodifiableList(sections.stream()
                .sorted(Comparator.comparing(Section::getId))
                .collect(Collectors.toList())
        );
    }

    public Set<SimpleStation> toSet() {
        Set<SimpleStation> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(new SimpleStation(section.getUpStationId()));
            stations.add(new SimpleStation(section.getDownStationId()));
        }
        return stations;
    }
}
