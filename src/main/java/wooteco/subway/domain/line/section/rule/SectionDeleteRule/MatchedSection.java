package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import wooteco.subway.domain.line.section.Section;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MatchedSection {

    private final Section sectionWithStationIdAsUp;
    private final Section sectionWithStationIdAsDown;

    public MatchedSection(List<Section> sections, Long stationId) {
        this.sectionWithStationIdAsUp = findMatchedSectionWithUpSection(sections, stationId).orElse(null);
        this.sectionWithStationIdAsDown = findMatchedSectionWithDownSection(sections, stationId).orElse(null);
    }

    private Optional<Section> findMatchedSectionWithUpSection(List<Section> sections, Long stationId) {
        return sections.stream()
                .filter(section -> section.hasSameUpStationId(stationId))
                .findAny();
    }

    private Optional<Section> findMatchedSectionWithDownSection(List<Section> sections, Long stationId) {
        return sections.stream()
                .filter(section -> section.hasSameDownStationId(stationId))
                .findAny();
    }

    public boolean isUpStationDelete() {
        return sectionWithStationIdAsUp != null && sectionWithStationIdAsDown == null;
    }

    public boolean isDownStationDelete() {
        return sectionWithStationIdAsUp == null && sectionWithStationIdAsDown != null;
    }

    public boolean isMiddleStationsDelete() {
        return sectionWithStationIdAsUp != null & sectionWithStationIdAsDown != null;
    }

    public Section getSectionWithStationIdAsUp() {
        return sectionWithStationIdAsUp;
    }

    public Section getSectionWithStationIdAsDown() {
        return sectionWithStationIdAsDown;
    }

}
