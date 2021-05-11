package wooteco.subway.section;

import wooteco.subway.exception.InvalidAddSectionException;

import java.util.*;
import java.util.function.BiPredicate;

import static java.util.stream.Collectors.toList;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validate(Section section) {
        validateConnected(section, this::isConnected);
        validateConnected(section, this::isNotExisted);
        validateDistance(section);
    }

    private void validateConnected(Section newSection, BiPredicate<Section, Section> biPredicate) {
        sections.stream()
            .filter(section -> biPredicate.test(section, newSection))
            .findAny()
            .orElseThrow(InvalidAddSectionException::new);
    }

    private boolean isConnected(Section newSection, Section section) {
        return isEndpoint(newSection, section) || isIntermediate(newSection, section);
    }

    private boolean isEndpoint(Section newSection, Section section) {
        return section.isUpStation(newSection.getDownStationId()) ||
            section.isDownStation(newSection.getUpStationId());
    }

    private boolean isIntermediate(Section newSection, Section section) {
        return section.isUpStation(newSection.getUpStationId()) ||
                section.isDownStation(newSection.getDownStationId());
    }

    private boolean isNotExisted(Section newSection, Section section) {
        return !(section.isUpStation(newSection.getUpStationId()) && section.isDownStation(newSection.getDownStationId())) ||
                !(section.isUpStation(newSection.getDownStationId()) && section.isDownStation(newSection.getUpStationId()));
    }

    private void validateDistance(Section newSection) {
        sections.stream()
            .filter(section -> section.isUpStation(newSection.getUpStationId()))
            .findAny()
            .ifPresent(section -> isValidDistance(newSection, section));
    }

    private void isValidDistance(Section newSection, Section section) {
        if (section.isSameOrLongDistance(newSection)) {
            throw new InvalidAddSectionException();
        }
    }

    public List<Long> sortedStationIds() {
        Deque<Long> sortedStationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        initializeByIds(sortedStationIds, upStationIds, downStationIds);
        sortByIds(sortedStationIds, upStationIds, downStationIds);

        return new ArrayList<>(sortedStationIds);
    }

    private void initializeByIds(Deque<Long> sortedStationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        for (Section section : sections) {
            upStationIds.put(section.getUpStationId(), section.getDownStationId());
            downStationIds.put(section.getDownStationId(), section.getUpStationId());
        }

        Section now = sections.get(0);
        sortedStationIds.add(now.getUpStationId());
    }

    private void sortByIds(Deque<Long> sortedStationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        while (upStationIds.containsKey(sortedStationIds.peekFirst())) {
            Long currentId = sortedStationIds.peekFirst();
            sortedStationIds.addFirst(downStationIds.get(currentId));
        }
        while (downStationIds.containsKey(sortedStationIds.peekLast())) {
            Long currentId = sortedStationIds.peekLast();
            sortedStationIds.addLast(upStationIds.get(currentId));
        }
    }

    public boolean isBiggerThanOne() {
        return sections.size() > 1;
    }

    public boolean isOne() {
        return sections.size() == 1;
    }

    public Section merge(Long stationId) {
        int newDistance = sections.stream()
            .mapToInt(Section::getDistance)
            .sum();

        Section upSection = getSection(stationId, this::matchDownStation);
        Section downSection = getSection(stationId, this::matchUpStation);

        return new Section(upSection.getUpStationId(), downSection.getDownStationId(), newDistance);
    }

    private Section getSection(Long stationId, BiPredicate<Long, Section> biPredicate) {
        return sections.stream()
            .filter(section -> biPredicate.test(stationId, section))
            .findAny()
            .get();
    }

    private boolean matchUpStation(Long stationId, Section section) {
        return section.isUpStation(stationId);
    }

    private boolean matchDownStation(Long stationId, Section section) {
        return section.isDownStation(stationId);
    }

    public List<Long> sectionIds() {
        return sections.stream()
            .map(Section::getId)
            .collect(toList());
    }
}
