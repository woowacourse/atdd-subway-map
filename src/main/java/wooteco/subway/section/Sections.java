package wooteco.subway.section;

import java.util.List;

public class Sections {
    private static final int MIDDLE_SECTION_CRITERIA = 2;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isNotEndPoint() {
        return sections.size() == MIDDLE_SECTION_CRITERIA;
    }

    public Long findUpStationId(Long stationId) {
        return sections.stream()
            .filter(section -> stationId != section.getUpStationId())
            .findAny()
            .get()
            .getUpStationId();
    }

    public long findDownStationId(Long stationId) {
        return sections.stream()
            .filter(section -> stationId != section.getDownStationId())
            .findAny()
            .get()
            .getUpStationId();
    }

    public int sumDistance() {
        return sections.stream()
            .mapToInt(Section::getDistance)
            .sum();
    }
}
