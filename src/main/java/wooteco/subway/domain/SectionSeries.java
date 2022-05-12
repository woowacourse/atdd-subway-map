package wooteco.subway.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SectionSeries {
    private final List<Section> sections;

    public SectionSeries(List<Section> sections) {
        this.sections = sections;
    }

    public EnrollSections findEnrollSections(Section newSection) {
        return new EnrollSections(newSection, findUpdateSection(newSection));
    }

    public Optional<Section> findUpdateSection(Section newSection) {
        if (isTerminal(newSection)) {
            return Optional.empty();
        }
        final Section intermediateSection = findIntermediateSection(newSection);
        return Optional.of(intermediateSection.reconnect(newSection));
    }

    private boolean isTerminal(Section newSection) {
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
            .collect(findOneCertainly());
    }

    private <T> Collector<T, ?, T> findOneCertainly() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                if (list.size() == 1) {
                    return list.get(0);
                }
                throw new RuntimeException("not connecetd");
            }
        );
    }

    public RemoveSections findDeleteSections(Long stationId) {
        return null;
    }
}
