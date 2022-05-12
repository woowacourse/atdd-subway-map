package wooteco.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Sections {
    private final List<Section> sections;

    public void save(Section section) {
        checkSavable(section);
        sections.add(section);
    }

    public Optional<Section> fixOverLappedSection(Section section) {
        Optional<Section> overLappedSection = getSectionOverLappedBy(section);

        if (overLappedSection.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(reviseSection(overLappedSection.get(), section));
    }

    private Section reviseSection(Section overLappedSection, Section section) {
        Section revisedSection = overLappedSection.revisedBy(section);

        sections.remove(section);
        sections.add(revisedSection);

        return revisedSection;
    }

    private void checkSavable(Section section) {
        checkExistence(section);
        checkConnected(section);
        checkDistance(section);
    }

    private void checkExistence(Section section) {
        if (isLineAlreadyHasBothStationsOf(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 존재합니다.");
        }
    }

    private boolean isLineAlreadyHasBothStationsOf(Section section) {
        Long lineId = section.getLineId();

        return existByLineAndStation(lineId, section.getUpStationId())
                && existByLineAndStation(lineId, section.getDownStationId());
    }

    private boolean existByLineAndStation(Long lineId, Long stationId) {
        return sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .anyMatch(section -> section.hasStation(stationId));
    }

    private void checkConnected(Section section) {
        if (!existConnectedTo(section)) {
            throw new IllegalArgumentException("기존 노선과 연결된 구간이 아닙니다.");
        }
    }

    private boolean existConnectedTo(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isConnectedTo(newSection));
    }

    private void checkDistance(Section newSection) {
        Optional<Section> overLappedSection = getSectionOverLappedBy(newSection);

        if (overLappedSection.isPresent() && newSection.isLongerThan(overLappedSection.get())) {
            throw new IllegalArgumentException("적절한 거리가 아닙니다.");
        }
    }

    private Optional<Section> getSectionOverLappedBy(Section newSection) {
        return sections.stream()
                .filter(section -> section.isOverLappedWith(newSection))
                .filter(section -> !section.hasSameValue(newSection))
                .findFirst();
    }

    public void delete(Long lineId, Long stationId) {
        checkDelete(lineId);

        List<Section> adjacentSections = sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .filter(section -> section.hasStation(stationId))
                .collect(Collectors.toList());

        adjacentSections.forEach(sections::remove);
    }

    private void checkDelete(Long lineId) {
        if (isLastSection(lineId)) {
            throw new IllegalArgumentException("노선의 유일한 구간은 삭제할 수 없습니다.");
        }
    }

    private boolean isLastSection(Long lineId) {
        return sections.stream()
                .filter(section -> lineId.equals(section.getLineId()))
                .count() == 1;
    }

    public Optional<Section> fixDisconnectedSection(Long lineId, Long stationId) {
        Optional<Section> upSection = sections.stream().
                filter(section -> lineId.equals(section.getLineId()) && stationId.equals(section.getDownStationId()))
                .findFirst();
        Optional<Section> downSection = sections.stream().
                filter(section -> lineId.equals(section.getLineId()) && stationId.equals(section.getUpStationId()))
                .findFirst();

        if (upSection.isEmpty() || downSection.isEmpty()) {
            return Optional.empty();
        }

        Section connectedSection = createConnectedSection(lineId, upSection.get(), downSection.get());
        sections.add(connectedSection);
        return Optional.of(connectedSection);
    }

    private Section createConnectedSection(Long lineId, Section upSection, Section downSection) {
        return new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance());
    }

    public Set<Long> getStationIds() {
        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        return stationIds;
    }

    public List<Station> sort(List<Station> stations) {
        Queue<Section> sortedSections = sortSections();
        return sortStations(stations, sortedSections);
    }

    private Queue<Section> sortSections() {
        Queue<Section> sortedSections = new LinkedList<>();
        Section startSection = findStartSection();
        sortedSections.add(startSection);

        return findAndAdd(sortedSections, new ArrayList<>(sections), startSection);
    }

    private Section findStartSection() {
        Long startStationId = findStartStationId();
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(startStationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));
    }

    private Queue<Section> findAndAdd(Queue<Section> sortedSections, List<Section> remainSections, Section currentSection) {
        remainSections.remove(currentSection);

        if(remainSections.isEmpty()){
            return sortedSections;
        }

        Section nextSection = remainSections.stream()
                .filter(section -> Objects.equals(currentSection.getDownStationId(), section.getUpStationId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));

        sortedSections.add(nextSection);
        return findAndAdd(sortedSections, remainSections, nextSection);
    }

    private List<Station> sortStations(List<Station> stations, Queue<Section> sortedSections) {
        List<Long> sortedStationsIds = new ArrayList<>();
        sortedStationsIds.add(findStartStationId());
        sortedStationsIds = pollAndAdd(sortedStationsIds, sortedSections);

        return sortedStationsIds.stream()
                .map(stationId -> findStationById(stations, stationId))
                .collect(Collectors.toList());
    }

    private List<Long> pollAndAdd(List<Long> sortedStationIds, Queue<Section> sortedSections) {
        if(sortedSections.isEmpty()){
            return sortedStationIds;
        }

        Section section = sortedSections.poll();
        sortedStationIds.add(section.getDownStationId());
        return pollAndAdd(sortedStationIds, sortedSections);
    }

    private Station findStationById(List<Station> stations, Long stationId) {
        return stations.stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 역이 존재하지 않습니다."));
    }

    private Long findStartStationId() {
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
