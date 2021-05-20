package wooteco.subway.line.domain;

import wooteco.subway.line.domain.rule.FindSectionStrategy;
import wooteco.subway.station.domain.Station;

import java.util.*;
import java.util.stream.Collectors;

import static wooteco.subway.line.domain.Section.EMPTY;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Station registeredStation(Section anotherSection) {
        List<Station> stations = new ArrayList<>();
        if (hasStation(anotherSection.upStation())) {
            stations.add(anotherSection.upStation());
        }
        if (hasStation(anotherSection.downStation())) {
            stations.add(anotherSection.downStation());
        }
        if (stations.size() != 1) {
            throw new IllegalStateException("[ERROR] 노선에 등록할 구간의 역이 하나만 등록되어 있어야 합니다.");
        }
        return stations.get(0);
    }

    public Section findSectionWithStation(Station targetStation, List<FindSectionStrategy> findSectionStrategies) {
        return findSectionStrategies.stream()
                .map(findSectionStrategy -> findSectionStrategy.findSection(sections, targetStation))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElse(EMPTY);
    }

    public Section findSectionWithUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.hasUpStation(upStation))
                .findFirst()
                .orElse(EMPTY);
    }

    public Section findSectionWithDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.hasDownStation(downStation))
                .findFirst()
                .orElse(EMPTY);
    }

    private boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.has(station));
    }

    public List<Station> sortedStations() {
        Map<Station, Station> sectionMap = generateSectionMap(sections);
        Station keyStation = findUpStation(sectionMap);
        return sortStation(sectionMap, keyStation);
    }

    private Map<Station, Station> generateSectionMap(List<Section> sections) {
        Map<Station, Station> sectionMap = new HashMap<>();
        for (Section section : sections) {
            sectionMap.put(section.upStation(), section.downStation());
        }
        return sectionMap;
    }

    private Station findUpStation(Map<Station, Station> sectionMap) {
        Set<Station> upStations = new HashSet<>(sectionMap.keySet());
        Set<Station> downStations = new HashSet<>(sectionMap.values());
        upStations.removeAll(downStations);
        if (upStations.size() != 1) {
            throw new IllegalArgumentException("[ERROR] 구간들의 역 관계가 올바르지 않습니다.");
        }
        return upStations.iterator().next();
    }

    private List<Station> sortStation(Map<Station, Station> sectionMap, Station keyStation) {
        List<Station> sortedStations = new ArrayList<>();
        sortedStations.add(keyStation);
        while (sectionMap.containsKey(keyStation)) {
            Station nextKeyStation = sectionMap.get(keyStation);
            sortedStations.add(nextKeyStation);
            keyStation = nextKeyStation;
        }
        return sortedStations;
    }

    public int size() {
        return sections.size();
    }

    public List<Long> stationIds() {
        return sortedStations().stream()
                .map(Station::id)
                .collect(Collectors.toList());
    }
}
