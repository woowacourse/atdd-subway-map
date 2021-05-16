package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import wooteco.subway.exception.InvalidSectionDistanceException;
import wooteco.subway.exception.InvalidStationException;
import wooteco.subway.exception.NoneOrAllStationsExistingInLineException;

public class Sections {

    private static final int MIDDLE_SECTION_CRITERIA = 2;
    private static final int VALID_SECTION_DUPLICATE_STATION_ID_CRITERIA = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections(List<Section> sections, Section section) {
        validateSectionStations(section);
        validateSectionDistance(section);
        this.sections = sections;
    }

    public void validateSectionStations(Section newSection) {
        List<Long> stationIds = sections.stream()
            .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());

        List<Long> newSectionStationsId = Arrays.asList(newSection.getUpStationId(),
            newSection.getDownStationId());

        stationIds.retainAll(newSectionStationsId);
        if (stationIds.size() != VALID_SECTION_DUPLICATE_STATION_ID_CRITERIA) {
            throw new NoneOrAllStationsExistingInLineException();
        }
    }

    public void validateSectionDistance(Section newSection) {
        int distance = sectionToBeDivided(newSection).getDistance();
        if (newSection.largerThan(distance)) {
            throw new InvalidSectionDistanceException();
        }
    }

    public boolean isNotEndPoint() {
        return sections.size() == MIDDLE_SECTION_CRITERIA;
    }

    public Long findUpStationId(Long stationId) {
        return findStationId(section -> section.isUpStation(stationId));
    }

    public Long findDownStationId(Long stationId) {
        return findStationId(section -> section.isDownStation(stationId));
    }

    private long findStationId(Predicate<Section> predicate) {
        return sections.stream()
            .filter(predicate)
            .findAny()
            .orElseThrow(InvalidStationException::new)
            .getUpStationId();
    }

    public int sumDistance() {
        return sections.stream()
            .mapToInt(Section::getDistance)
            .sum();
    }

    public Section sectionToBeDivided(Section newSection) {
        return sections.stream()
            .filter(section -> hasSameUpOrDownStationId(newSection, section))
            .findAny()
            .orElseThrow(InvalidStationException::new);
    }

    public Section divideSection(Long lindId, Section newSection) {
        Section existingSection = sectionToBeDivided(newSection);
        if (existingSection.isUpStation(newSection.getUpStationId())) {
            return new Section(lindId, newSection.getDownStationId(),
                existingSection.getDownStationId(), existingSection.deductDistance(newSection));
        }
        return new Section(lindId, existingSection.getUpStationId(), newSection.getUpStationId(),
            existingSection.deductDistance(newSection));
    }

    private boolean hasSameUpOrDownStationId(Section newSection, Section section) {
        return section.getUpStationId().equals(newSection.getUpStationId()) || section
            .getDownStationId().equals(newSection.getDownStationId());
    }

    public boolean isEmpty() {
        return sections.size() == 0;
    }

    public List<Long> sortedStationIds() {
        Deque<Long> sortedIds = new ArrayDeque<>();
        Map<Long, Long> upIds = new LinkedHashMap<>();
        Map<Long, Long> downIds = new LinkedHashMap<>();

        initializeByIds(sortedIds, upIds, downIds);
        sort(sortedIds, upIds, downIds);

        return new ArrayList<>(sortedIds);
    }

    private void initializeByIds(Deque<Long> sortedIds, Map<Long, Long> upIds,
        Map<Long, Long> downIds) {
        for (Section section : sections) {
            upIds.put(section.getDownStationId(), section.getUpStationId());
            downIds.put(section.getUpStationId(), section.getDownStationId());
        }

        Section now = sections.get(0);
        sortedIds.addFirst(now.getUpStationId());
    }

    private void sort(Deque<Long> sortedIds, Map<Long, Long> upIds, Map<Long, Long> downIds) {
        while (upIds.containsKey(sortedIds.peekFirst())) {
            Long currentId = sortedIds.peekFirst();
            sortedIds.addFirst(upIds.get(currentId));
        }
        while (downIds.containsKey(sortedIds.peekLast())) {
            Long currentId = sortedIds.peekLast();
            sortedIds.addLast(downIds.get(currentId));
        }
    }
}
