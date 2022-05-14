package wooteco.subway.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wooteco.subway.util.CollectorsUtils;

public class SectionSeries {
    private final List<Section> sections;

    public SectionSeries(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> add(Section section) {
        if (sections.isEmpty()) {
            return addInitial(section);
        }
        return insert(section);
    }

    private List<Section> addInitial(Section section) {
        this.sections.add(section);
        return List.of(section);
    }

    private List<Section> insert(Section newSection) {
        if (isAppending(newSection)) {
            return List.of(newSection);
        }

        final Section findSection = findIntermediateSection(newSection);
        return List.of(findIntermediateSection(newSection), newSection, findSection.divide(newSection)); // TODO
    }

    private boolean isAppending(Section newSection) {
        final Map<Station, Station> sectionMap = sections.stream()
            .collect(Collectors.toMap(
                Section::getUpStation,
                Section::getDownStation));
        return doesTerminalExist(newSection, sectionMap);
    }

    private boolean doesTerminalExist(Section newSection, Map<Station, Station> sectionMap) {
        return !sectionMap.containsKey(newSection.getUpStation()) &&
            !sectionMap.containsValue(newSection.getDownStation()) &&
            (sectionMap.containsKey(newSection.getDownStation()) ||
                sectionMap.containsValue(newSection.getUpStation()));
    }

    private Section findIntermediateSection(Section newSection) {
        return sections.stream()
            .filter(section -> section.isDividable(newSection))
            .collect(CollectorsUtils.findOneCertainly());
    }

    public List<Section> remove(Long stationId) {
        final List<Section> relatedSections = sections.stream()
            .filter(section -> section.isAnyIdMatch(stationId))
            .collect(Collectors.toList());

        if (relatedSections.size() == 2) {
            return List.of(relatedSections.get(0),
                relatedSections.get(1),
                new Section(relatedSections.get(0).getUpStation(), relatedSections.get(1).getDownStation(),
                    relatedSections.get(0).getDistance().plus(relatedSections.get(1).getDistance())));
        }
        return List.of(relatedSections.get(0));
    }

    public List<Section> getSections() {
        return sections;
    }
}
