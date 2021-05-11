package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.lang.reflect.Array;
import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    // TODO :
    //  존재하면 sectionAddRequest의 upstationId로 section을 찾고
    //  찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우는 예외다.
    //  찾은 section의 upstationId를 sectionAddRequest의 downStationId로 수정한다.
    //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.
    // TODO :
    //  존재하면 sectionAddRequest의 downStationId로 section을 찾고
    //  찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우는 예외다.
    //  찾은 section의 downStationId를 sectionAddRequest의 upStationId로 수정한다.
    //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.

    public Sections addedSections(Section anotherSection) {
        checkAbleToAddSection(anotherSection);
        Station upStation = anotherSection.upStation();
        Station downStation = anotherSection.downStation();
        if (sortedStations().contains(upStation)) {
            Section targetSection = findSectionWithUpStation(upStation);
            checkAbleToAddByDistance(anotherSection, targetSection);
            targetSection.changeUpStation(anotherSection.downStation());
            targetSection.subDistance(anotherSection.distance());
            return new Sections(new ArrayList<>(sections));
        }

        Section targetSection = findSectionWithDownStation(downStation);
        checkAbleToAddByDistance(anotherSection, targetSection);
        targetSection.changeUpStation(anotherSection.upStation());
        targetSection.subDistance(anotherSection.distance());
        return new Sections(new ArrayList<>(sections));

    }

    private void checkAbleToAddByDistance(Section anotherSection, Section targetSection) {
        if (targetSection.lessDistanceThan(anotherSection)) {
            throw new IllegalArgumentException("[ERROR] 기존 구간 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }

    private Section findSectionWithUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.hasUpStation(upStation))
                .findFirst()
                .get();
    }

    private Section findSectionWithDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.hasDownStation(downStation))
                .findFirst()
                .get();
    }

    private void checkAbleToAddSection(Section section) {
        if (!isOnlyOneRegistered(section)) {
            throw new IllegalStateException("[ERROR] 노선에 등록할 구간의 역이 하나만 등록되어 있어야 합니다.");
        }
    }

    public boolean isOnlyOneRegistered(Section anotherSection) {
        boolean hasUpStation = hasStation(anotherSection.upStation());
        boolean hasDownStation = hasStation(anotherSection.downStation());
        return (hasUpStation && !hasDownStation) || (!hasUpStation && hasDownStation);

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

    public Section affectedSection(Sections originSections) {
        sections.removeAll(originSections.sections);
        return sections.get(0);
    }
}
