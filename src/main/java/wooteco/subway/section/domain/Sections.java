package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

import java.util.*;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(sections);
    }

    public List<Section> sections() {
        return sections;
    }

    public Section getModifiedSectionIfCanAdd(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        return sections.stream()
                .filter(section -> section.hasSameUpStation(upStation) && !hasAnotherSameStation(downStation)
                        || section.hasSameDownStation(downStation) && !hasAnotherSameStation(upStation))
                .filter(section -> section.isLessDistance(newSection))
                .map(section -> section.changeSection(newSection))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("구간을 추가할 수 없습니다."));
    }

    public boolean isUpLastSection(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        return sections.get(0).hasSameUpStation(downStation) && !hasAnotherSameStation(upStation);
    }

    public boolean isDownLastSection(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        return sections.get(sections.size() - 1).hasSameDownStation(upStation) && !hasAnotherSameStation(downStation);
    }

    private boolean hasAnotherSameStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    private List<Section> sort(List<Section> sections) {
        if (sections.size() == 1) {
            return sections;
        }
        Map<Station, Section> upStations = new HashMap<>();
        Map<Station, Section> downStations = new HashMap<>();
        Map<Station, Section> tmpUpStations = new HashMap<>();
        checkStations(sections, upStations, downStations, tmpUpStations);

        List<Section> sortedSections = new ArrayList<>();
        makeSortedSections(upStations, tmpUpStations, sortedSections);
        return sortedSections;

    }

    private void makeSortedSections(Map<Station, Section> upStations, Map<Station, Section> tmpUpStations, List<Section> sortedSections) {
        Section firstSection = upStations.keySet().stream()
                .map(key -> upStations.get(key))
                .findFirst().get();
        sortedSections.add(firstSection);
        while (tmpUpStations.containsKey(firstSection.getDownStation())) {
            sortedSections.add(tmpUpStations.get(firstSection.getDownStation()));
            firstSection = tmpUpStations.get(firstSection.getDownStation());
        }
    }

    private void checkStations(List<Section> sections, Map<Station, Section> upStations, Map<Station, Section> downStations, Map<Station, Section> tmpUpStations) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            upStations.put(upStation, section);
            tmpUpStations.put(upStation, section);
            downStations.put(downStation, section);

            upStations.remove(downStation);
            downStations.remove(upStation);
        }
    }
}