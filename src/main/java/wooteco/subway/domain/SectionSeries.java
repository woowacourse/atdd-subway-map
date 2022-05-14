package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wooteco.subway.exception.RowNotFoundException;
import wooteco.subway.exception.SectionNotEnoughException;
import wooteco.subway.util.CollectorsUtils;

public class SectionSeries {
    private final List<Section> sections;

    public SectionSeries(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        if (sections.isEmpty() || isAppending(section)) {
            this.sections.add(section);
            return ;
        }
        insert(section);
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

    private void insert(Section newSection) {
        final Section findSection = findIntermediateSection(newSection);
        this.sections.remove(findSection);
        this.sections.add(newSection);
        this.sections.add(findSection.divide(newSection));
    }

    private Section findIntermediateSection(Section newSection) {
        return sections.stream()
            .filter(section -> section.isDividable(newSection))
            .collect(CollectorsUtils.findOneCertainly())
            .orElseThrow(() -> new RowNotFoundException(
                String.format("%s 혹은 %s와 같은 방향의 구간을 찾지 못했습니다.",
                    newSection.getUpStation().getName(),
                    newSection.getDownStation().getName())
            ));
    }

    public void remove(Long stationId) {
        validateSectionEnough();
        final List<Section> relatedSections = sections.stream()
            .filter(section -> section.isAnyIdMatch(stationId))
            .collect(Collectors.toList());

        if (relatedSections.size() == 1) {
            this.sections.remove(relatedSections.get(0));
            return;
        }
        if (relatedSections.size() == 2) {
            this.sections.remove(relatedSections.get(0));
            this.sections.remove(relatedSections.get(1));
            this.sections.add(
                new Section(relatedSections.get(0).getUpStation(),
                    relatedSections.get(1).getDownStation(),
                    relatedSections.get(0).getDistance().plus(relatedSections.get(1).getDistance())
                ));
        }
    }

    private void validateSectionEnough() {
        if (this.sections.size() == 1) {
            throw new SectionNotEnoughException("구간이 하나인 경우에는 삭제할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
