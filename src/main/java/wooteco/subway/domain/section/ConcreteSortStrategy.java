package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;

import java.util.*;

public class ConcreteSortStrategy implements SortStrategy {

    public List<Station> sort(List<Section> sections, List<Station> stations) {
        Queue<Section> sortedSections = sortSections(sections);
        return sortStations(sections, stations, sortedSections);
    }

    private Queue<Section> sortSections(List<Section> sections) {
        Queue<Section> sortedSections = new LinkedList<>();
        Section startSection = findStartSection(sections);
        sortedSections.add(startSection);

        return findAndAdd(sortedSections, new ArrayList<>(sections), startSection);
    }

    private Section findStartSection(List<Section> sections) {
        Long startStationId = findStartStationId(sections);
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(startStationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));
    }

    private Queue<Section> findAndAdd(Queue<Section> sortedSections, List<Section> remainSections, Section currentSection) {
        remainSections.remove(currentSection);
        if (remainSections.isEmpty()) {
            return sortedSections;
        }

        Section nextSection = remainSections.stream()
                .filter(section -> Objects.equals(currentSection.getDownStationId(), section.getUpStationId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));

        sortedSections.add(nextSection);
        return findAndAdd(sortedSections, remainSections, nextSection);
    }

    private List<Station> sortStations(List<Section> sections, List<Station> stations, Queue<Section> sortedSections) {
        Queue<Long> sortedStationsIds = new LinkedList<>();
        sortedStationsIds.add(findStartStationId(sections));
        sortedStationsIds = pollAndAdd(sortedStationsIds, sortedSections);

        return createStationsByIds(sortedStationsIds, stations);
    }

    private Queue<Long> pollAndAdd(Queue<Long> sortedStationIds, Queue<Section> sortedSections) {
        if (sortedSections.isEmpty()) {
            return sortedStationIds;
        }
        Section section = sortedSections.poll();
        sortedStationIds.add(section.getDownStationId());
        return pollAndAdd(sortedStationIds, sortedSections);
    }

    private List<Station> createStationsByIds(Queue<Long> stationIds, List<Station> stations) {
        List<Station> newStations = new ArrayList<>();
        while (!stations.isEmpty()) {
            Station station = findStationById(stations, stationIds.poll());
            newStations.add(station);
            stations.remove(station);
        }
        return newStations;
    }

    private Station findStationById(List<Station> stations, Long stationId) {
        return stations.stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 역이 존재하지 않습니다."));
    }

    private Long findStartStationId(List<Section> sections) {
        List<Long> upStationIds = new ArrayList<>();
        List<Long> downStationIds = new ArrayList<>();

        for (Section section : sections) {
            upStationIds.add(section.getUpStationId());
            downStationIds.add(section.getDownStationId());
        }
        upStationIds.removeAll(downStationIds);
        return upStationIds.get(0);
    }
}
