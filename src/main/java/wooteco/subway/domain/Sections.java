package wooteco.subway.domain;

import wooteco.subway.exception.section.SectionAlreadyExistBothStationException;
import wooteco.subway.exception.section.SectionNotExistBothStationException;
import wooteco.subway.exception.section.SectionNotExistException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Deque<Long> getSortedStationIds() {
        Deque<Long> sortedStationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        for (Section section : sections) {
            upStationIds.put(section.getDownStationId(), section.getUpStationId());
            downStationIds.put(section.getUpStationId(), section.getDownStationId());
        }
        Section now = sections.get(0);
        sortedStationIds.addFirst(now.getUpStationId());

        while (upStationIds.containsKey(sortedStationIds.peekFirst())) {
            Long currentId = sortedStationIds.peekFirst();
            sortedStationIds.addFirst(upStationIds.get(currentId));
        }

        while (downStationIds.containsKey(sortedStationIds.peekLast())) {
            Long currentId = sortedStationIds.peekLast();
            sortedStationIds.addLast(downStationIds.get(currentId));
        }
        return sortedStationIds;
    }

    public void validate(Section section) {
        Deque<Long> stationIdsInOrder = getSortedStationIds();
        if (stationIdsInOrder.contains(section.getUpStationId()) && stationIdsInOrder.contains(section.getDownStationId())) {
            throw new SectionAlreadyExistBothStationException(section);
        }

        if (!stationIdsInOrder.contains(section.getUpStationId()) && !stationIdsInOrder.contains(section.getDownStationId())) {
            throw new SectionNotExistBothStationException(section);
        }
    }

    private List<Long> getUpStationIds() {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    private List<Long> getDownStationIds() {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public Section getPreviousSection(Section newSection) {
        return sections.stream()
                .filter(section -> newSection.getUpStationId().equals(section.getUpStationId()))
                .findAny()
                .orElseThrow(SectionNotExistException::new);
    }

    public Section getFollowingSection(Section newSection) {
        return sections.stream()
                .filter(section -> newSection.getDownStationId().equals(section.getDownStationId()))
                .findAny()
                .orElseThrow(SectionNotExistException::new);
    }

    public boolean isExistInUpStationIds(Long stationId) {
        return getUpStationIds().contains(stationId);
    }

    public boolean isExistInDownStationIds(Long stationId) {
        return getDownStationIds().contains(stationId);
    }
}
