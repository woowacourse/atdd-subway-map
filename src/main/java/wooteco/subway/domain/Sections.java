package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();

        for (Section section: sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return stationIds.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
