package wooteco.subway.domain.line.section;

import wooteco.subway.domain.line.section.rule.SectionAddRule.SectionAddRule;
import wooteco.subway.domain.line.section.rule.SectionAddRule.SectionAddRuleFactory;
import wooteco.subway.domain.line.section.rule.SectionDeleteRule.SectionDeleteRule;
import wooteco.subway.domain.line.section.rule.SectionDeleteRule.SectionDeleteRuleFactory;
import wooteco.subway.exception.line.AlreadyExistingUpAndDownStationsException;
import wooteco.subway.exception.line.ConnectableStationNotFoundException;
import wooteco.subway.exception.line.SectionDeleteException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Sections {

    private final List<Section> sections;
    private final List<SectionAddRule> sectionAddRules;
    private final List<SectionDeleteRule> sectionDeleteRules;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        this.sectionAddRules = SectionAddRuleFactory.create();
        this.sectionDeleteRules = SectionDeleteRuleFactory.create();
    }

    public static Sections empty() {
        return new Sections(Collections.emptyList());
    }

    public void add(Section section) {
        validateThatConnectableStationIsExisting(section);
        validateThatSectionIsAlreadyExisting(section);

        sectionAddRules.stream()
                .filter(rule -> rule.isSatisfiedBy(sections, section))
                .findAny()
                .ifPresent(rule -> rule.execute(sections, section));
    }

    private void validateThatSectionIsAlreadyExisting(Section newSection) {
        List<Long> stationIds = new OrderedStationId(sections).asList();
        int indexOfUpStationId = stationIds.indexOf(newSection.getUpStationId());
        int indexOfDownStationId = stationIds.indexOf(newSection.getDownStationId());

        if (indexOfUpStationId != -1 && indexOfDownStationId != -1) {
            throw new AlreadyExistingUpAndDownStationsException();
        }
    }

    private void validateThatConnectableStationIsExisting(Section newSection) {
        List<Long> stationIds = getAllStationIds();

        if (!stationIds.contains(newSection.getDownStationId()) &&
                !stationIds.contains(newSection.getUpStationId())) {
            throw new ConnectableStationNotFoundException();
        }
    }

    private List<Long> getAllStationIds() {
        return sections.stream()
                .flatMap(section ->
                        Stream.of(
                                section.getUpStationId(),
                                section.getDownStationId()
                        )
                )
                .distinct()
                .collect(toList());
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public List<Long> getStationIds() {
        return new OrderedStationId(sections).asList();
    }

    public void deleteSectionByStationId(Long stationId) {
        validateThatThereIsOnlyOneSection();

        sectionDeleteRules.stream()
                .filter(
                        sectionDeleteRule ->
                                sectionDeleteRule.isSatisfiedBy(sections, stationId)
                )
                .findAny()
                .ifPresent(
                        sectionDeleteRule ->
                                sectionDeleteRule.execute(sections, stationId)
                );
    }

    private void validateThatThereIsOnlyOneSection() {
        if (sections.size() == 1) {
            throw new SectionDeleteException();
        }
    }

    public List<Long> getUnOrderedStationIds() {
        return sections.stream()
                .flatMap(section -> Stream.of(
                                section.getUpStationId(),
                                section.getDownStationId()
                        ))
                .distinct()
                .collect(toList());
    }

}
