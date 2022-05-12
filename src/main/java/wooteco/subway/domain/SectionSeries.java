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
        if (isAppending(newSection)) {
            return Optional.empty();
        }
        final Section intermediateSection = findIntermediateSection(newSection);
        return Optional.of(intermediateSection.divide(newSection));
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
            .collect(findOneCertainly());
    }

    private <T> Collector<T, ?, T> findOneCertainly() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                if (list.size() == 1) {
                    return list.get(0);
                }
                throw new RuntimeException("not connecetd"); // TODO : fix to custom exception
            }
        );
    }

    public RemoveSections findRemoveSections(Long stationId) {
        final List<Section> relatedSections = sections.stream()
            .filter(section -> section.isAnyIdMatch(stationId))
            .collect(Collectors.toList());
        if (relatedSections.size() == 2) {
            return deleteIntermediate(relatedSections.get(0), relatedSections.get(1));
        }
        if (relatedSections.size() == 1) {
            return deleteTerminal(relatedSections.get(0));
        }
        throw new RuntimeException("delete error");
        // 상행 종점이면 업섹션을 삭제 // 하행 종점이면 다운섹션을 삭제하고, 수정 X
        // 중간이면 상행 하행 상관이 없이, 다운섹션 삭제하고, 업섹션 수정
    }

    private RemoveSections deleteTerminal(Section section) {
        return new RemoveSections(section, Optional.empty());
    }

    private RemoveSections deleteIntermediate(Section firstSection, Section secondSection) {
        return new RemoveSections(secondSection, Optional.of(firstSection.merge(secondSection)));
    }

}
