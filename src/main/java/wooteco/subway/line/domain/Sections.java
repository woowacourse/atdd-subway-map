package wooteco.subway.line.domain;

import wooteco.subway.line.domain.rule.FindSectionRule;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    public static final String ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE = "구간의 길이가 기존 구간 길이보다 크거나 같을 수 없습니다.";
    public static final String ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE = "상행역과 하행역 둘 중 하나만 노선에 존재해야 합니다.";

    private final LinkedList<Section> sections;

    public Sections(final List<Section> sections) {
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

    public Section findDeleteByAdding(Section newSection, List<FindSectionRule> findSectionRules) {
        for (FindSectionRule rule : findSectionRules) {
            Optional<Section> section = rule.findSection(this, newSection);
            if (section.isPresent()) {
                return section.get();
            }
        }

        throw new IllegalArgumentException(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE);
    }

    public boolean checkEndPoint(Section section) {
        Section upSection = sections.getFirst();
        Section downSection = sections.getLast();
        return section.getDownStationId().equals(upSection.getUpStationId())
                || section.getUpStationId().equals(downSection.getDownStationId());
    }

    public int sumSectionDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }

    private boolean hasStation(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId))
                .count() >= 1;
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

    private List<Long> getUpStationIdList(List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    private List<Long> getDownStationIdList(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public Section generateUpdateWhenAdd(final Section newSection, final Section deleteSection) {
        int distance = deleteSection.getDistance() - newSection.getDistance();

        if (distance <= 0) {
            throw new IllegalArgumentException(ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE);
        }

        if (newSection.getUpStationId().equals(deleteSection.getUpStationId())) {
            return new Section(newSection.getDownStationId(), deleteSection.getDownStationId(), distance);
        }

        if (newSection.getDownStationId().equals(deleteSection.getDownStationId())) {
            return new Section(deleteSection.getUpStationId(), newSection.getUpStationId(), distance);
        }

        throw new IllegalArgumentException("노선의 상행역 혹은 하행역을 찾을 수 없습니다.");
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
