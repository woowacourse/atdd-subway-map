package wooteco.subway.line.domain;

import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> toList() {
        return sections;
    }

    public int sumSectionDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }

    public boolean hasStation(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId))
                .count() == 1;
    }
}
