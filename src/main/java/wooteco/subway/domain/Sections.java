package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.exception.ExceptionMessage;

public class Sections {

    private static final int MINIMUM_SECTIONS_FOR_DELETE = 2;
    private static final int NO_UPPER_SECTION_EXISTS = 0;
    private static final int MERGEABLE_SECTION_COUNT = 2;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        if (sections == null) {
            this.sections = new ArrayList<>();
            return;
        }
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        checkInsertSectionsStations(section);
        findDividableSection(section)
                .ifPresent(it -> update(it, it.divideFrom(section)));
        sections.add(section);
    }

    private Optional<Section> findDividableSection(Section section) {
        return sections.stream()
                .filter(it -> it.isForDivide(section))
                .findFirst();
    }

    private void update(Section from, Section to) {
        sections.remove(from);
        sections.add(to);
    }

    private void checkInsertSectionsStations(Section section) {
        if (!sections.isEmpty() && isAlreadyConnected(section)) {
            throw new IllegalArgumentException(ExceptionMessage.INSERT_DUPLICATED_SECTION.getContent());
        }
        if (!sections.isEmpty() && unableConnect(section)) {
            throw new IllegalArgumentException(ExceptionMessage.INSERT_SECTION_NOT_MATCH.getContent());
        }
    }

    private boolean unableConnect(Section section) {
        List<Long> stationIds = getSortedStationId();
        return !stationIds.contains(section.getDownStationId())
                && !stationIds.contains(section.getUpStationId());
    }

    private boolean isAlreadyConnected(Section section) {
        List<Long> stationIds = getSortedStationId();
        return stationIds.contains(section.getDownStationId()) && stationIds.contains(section.getUpStationId());
    }

    public List<Long> getSortedStationId() {
        LinkedList<Section> unsorted = new LinkedList<>(sections);
        LinkedList<Section> sorted = new LinkedList<>();
        Section first = findFirst();

        sorted.offerLast(first);
        unsorted.remove(first);
        while (!unsorted.isEmpty()) {
            Section last = sorted.peekLast();
            Section next = findNext(last, unsorted);
            sorted.offerLast(next);
            unsorted.remove(next);
        }
        return getSortedStationId(sorted);
    }

    private Section findFirst() {
        return sections.stream()
                .filter(this::isFirstSection)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("구간이 순환되고 있습니다."));
    }

    private boolean isFirstSection(Section section) {
        return getUpperCount(section) == NO_UPPER_SECTION_EXISTS;
    }

    private long getUpperCount(Section section) {
        return sections.stream()
                .filter(it -> it.isUpperThan(section))
                .count();
    }

    private Section findNext(Section section, List<Section> sections) {
        return sections.stream()
                .filter(it -> it.isDownerThan(section))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간을 찾지 못했습니다."));
    }

    private List<Long> getSortedStationId(List<Section> sections) {
        Set<Long> distinctIds = new LinkedHashSet<>();
        for (Section section : sections) {
            distinctIds.add(section.getUpStationId());
            distinctIds.add(section.getDownStationId());
        }
        return new ArrayList<>(distinctIds);
    }

    public void deleteSectionsByStationId(Long stationId) {
        if (sections.size() < MINIMUM_SECTIONS_FOR_DELETE) {
            throw new IllegalArgumentException(ExceptionMessage.SECTIONS_NOT_DELETABLE.getContent());
        }

        removeNearSections(stationId);
        mergeSections().ifPresent(sections::add);
    }

    private void removeNearSections(Long stationId) {
        List<Section> nearSections = findNearSections(stationId);

        for (Section section : nearSections) {
            sections.remove(section);
        }
    }

    private List<Section> findNearSections(Long stationId) {
        return sections.stream()
                .filter(it -> it.hasStation(stationId))
                .collect(Collectors.toList());
    }

    private Optional<Section> mergeSections() {
        if (sections.size() < MERGEABLE_SECTION_COUNT) {
            return Optional.empty();
        }
        Section from = sections.get(0);
        Section to = sections.get(1);
        return Optional.of(from.merge(to));
    }

    public List<Section> getValue() {
        return new ArrayList<>(sections);
    }
}
