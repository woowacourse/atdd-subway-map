package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.SectionException;
import wooteco.subway.exception.notfound.SectionNotFoundException;

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
            throw new SectionException(ExceptionMessage.INSERT_DUPLICATED_SECTION.getContent());
        }
        if (!sections.isEmpty() && unableConnect(section)) {
            throw new SectionException(ExceptionMessage.INSERT_SECTION_NOT_MATCH.getContent());
        }
    }

    private boolean unableConnect(Section section) {
        List<Station> stations = getSortedStation();
        return !stations.contains(section.getDownStation())
                && !stations.contains(section.getUpStation());
    }

    private boolean isAlreadyConnected(Section section) {
        List<Station> station = getSortedStation();
        return station.contains(section.getDownStation()) && station.contains(section.getUpStation());
    }

    public List<Station> getSortedStation() {
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
        return getSortedStation(sorted);
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
                .orElseThrow(SectionNotFoundException::new);
    }

    private List<Station> getSortedStation(List<Section> sections) {
        Set<Station> distinctStations = new LinkedHashSet<>();
        for (Section section : sections) {
            distinctStations.add(section.getUpStation());
            distinctStations.add(section.getDownStation());
        }
        return new ArrayList<>(distinctStations);
    }

    public void deleteNearBy(Station station) {
        if (sections.size() < MINIMUM_SECTIONS_FOR_DELETE) {
            throw new SectionException(ExceptionMessage.SECTIONS_NOT_DELETABLE.getContent());
        }

        List<Section> nearSections = findNearSections(station);
        removeNearSections(nearSections);
        mergeSections(nearSections).ifPresent(sections::add);
    }

    private void removeNearSections(List<Section> nearSections) {
        for (Section section : nearSections) {
            sections.remove(section);
        }
    }

    private List<Section> findNearSections(Station station) {
        return sections.stream()
                .filter(it -> it.hasStation(station))
                .collect(Collectors.toList());
    }

    private Optional<Section> mergeSections(List<Section> nearSections) {
        if (nearSections.size() < MERGEABLE_SECTION_COUNT) {
            return Optional.empty();
        }
        Section from = nearSections.get(0);
        Section to = nearSections.get(1);
        return Optional.of(from.merge(to));
    }

    public List<Section> getValue() {
        return new ArrayList<>(sections);
    }
}
