package wooteco.subway.line.domain;

import wooteco.subway.line.domain.rule.FindSectionRule;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    public static final String ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE = "상행역과 하행역 둘 중 하나만 노선에 존재해야 합니다.";

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        if (sections.isEmpty()) {
            this.sections = sections;
            return;
        }

        this.sections = sort(sections);
    }

    public List<Section> toList() {
        return sections;
    }

    public void validateEnableAddSection(Section newSection) {
        if (hasStation(newSection.getUpStationId()) == hasStation(newSection.getDownStationId())) {
            throw new IllegalArgumentException(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE);
        }
    }

    public boolean checkEndPoint(Section section) {
        Section upSection = sections.get(0);
        Section downSection = sections.get(sections.size() - 1);
        return section.getDownStationId().equals(upSection.getUpStationId())
                || section.getUpStationId().equals(downSection.getDownStationId());
    }

    public Section findDeleteByAdding(Section newSection, List<FindSectionRule> findSectionRules) {
        for (FindSectionRule rule : findSectionRules) {
            Optional<Section> section = rule.findSection(this, newSection);
            if (section.isPresent()) {
                return section.get();
            }
        }

        throw new IllegalArgumentException(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE);
    }

    private boolean hasStation(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId))
                .count() >= 1;
    }

    public List<Section> deleteSection(final Long stationId) {
        if (sections.size() == 1) {
            throw  new IllegalArgumentException("삭제 할 수 없습니다. 현재 구간이 1개 입니다.");
        }

        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
    }

    public Section generateUpdateWhenDelete(final List<Section> deleteSections) {
        Section firstSection = deleteSections.get(0);
        Section secondSection = deleteSections.get(1);

        return getUpdateSectionWhenDelete(firstSection, secondSection);
    }

    private LinkedList<Section> sort(List<Section> sections) {
        LinkedList<Section> sortedSection = new LinkedList<>();
        Section firstSection = findStartSection(sections);
        sortedSection.add(firstSection);

        while (true) {
            Section lastSection = sortedSection.getLast();
            Optional<Section> nextSection = findNextSection(sections, lastSection.getDownStationId());

            if (!nextSection.isPresent()) {
                break;
            }

            sortedSection.add(nextSection.get());
        }
        return sortedSection;
    }


    private Optional<Section> findNextSection(List<Section> sections, Long downStationId) {
        return sections.stream()
                .filter(section -> downStationId.equals(section.getUpStationId()))
                .findFirst();

    }

    private Section findStartSection(List<Section> sections) {
        List<Long> downStationIdList = getDownStationIdList(sections);
        Optional<Section> startSection = sections.stream()
                .filter(section -> !downStationIdList.contains(section.getUpStationId()))
                .findFirst();

        if (startSection.isPresent()) {
            return startSection.get();
        }

        throw new IllegalArgumentException("노선의 상행역을 찾을 수 없습니다.");
    }

    private List<Long> getDownStationIdList(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    private Section getUpdateSectionWhenDelete(Section first, Section second) {
        int distance = first.getDistance() + second.getDistance();

        if (first.getDownStationId().equals(second.getUpStationId())) {
            return new Section(first.getUpStationId(), second.getDownStationId(), distance);
        }

        if (second.getDownStationId().equals(first.getUpStationId())) {
            return new Section(second.getUpStationId(), first.getDownStationId(), distance);
        }

        throw new IllegalArgumentException("노선을 삭제 할 수 없습니다.");
    }
}
