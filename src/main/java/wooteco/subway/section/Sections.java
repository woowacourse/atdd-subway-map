package wooteco.subway.section;

import wooteco.subway.station.Station;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private Long lineId;
    private List<Section> sections;

    public Sections(Long lineId, List<Section> sections) {
        validateProperSections(lineId, sections);
        this.lineId = lineId;
        this.sections = new ArrayList<>(sections);
    }

    private void validateProperSections(Long lineId, List<Section> sections) {
        final Optional<Section> unProperSection = sections.stream()
                .filter(section -> !section.getLineId().equals(lineId))
                .findAny();
        if (unProperSection.isPresent()) {
            throw new IllegalStateException("노선에 없는 구간이 포함되어 있습니다.");
        }
    }

    public List<Station> lineUpStations() {
        final Map<Station, Station> sectionConnectionInfo = generateSectionConnectionInfo();

        Station station = findUpEndStation();
        List<Station> lineUpStations = new ArrayList<>();
        while (sectionConnectionInfo.containsKey(station)) {
            lineUpStations.add(station);
            station = sectionConnectionInfo.get(station);
        }
        lineUpStations.add(station);

        return lineUpStations;
    }

    private Map<Station, Station> generateSectionConnectionInfo() {
        Map<Station, Station> sectionConnectionInfo = new HashMap<>();
        for (Section section : sections) {
            sectionConnectionInfo.put(section.getUpStation(), section.getDownStation());
        }
        return sectionConnectionInfo;
    }

    private Station findUpEndStation() {
        final List<Station> upStationsInSection = getUpStationsInSection();
        final List<Station> downStationsInSection = getDownStationsInSection();

        return upStationsInSection.stream()
                .filter(upStation -> !downStationsInSection.contains(upStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("상행 종점 조회에 실패했습니다"));
    }

    private List<Station> getUpStationsInSection() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStationsInSection() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public void insertSection(Section section) {
        if (insertSectionAtEdge(section)) {
            return;
        }
        insertSectionInBetween(section);
    }

    public boolean insertSectionAtEdge(Section section) {
        final List<Station> stations = lineUpStations();
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        validateConnection(stations, upStation, downStation);
        if (stations.get(0).equals(downStation) || stations.get(stations.size() - 1).equals(upStation)) {
            sections.add(section);
            return true;
        }
        return false;
    }

    public Map<Section, Section> insertSectionInBetween(Section section) {
        final List<Station> stations = lineUpStations();
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        validateConnection(stations, upStation, downStation);
        return insertSectionBetweenSections(stations, section, upStation, downStation);
    }

    private void validateConnection(List<Station> stationsInSection, Station upStation, Station downStation) {
        if ((stationsInSection.contains(upStation) && stationsInSection.contains(downStation)) ||
                (!stationsInSection.contains(upStation) && !stationsInSection.contains(downStation))) {
            throw new IllegalArgumentException("연결될 수 있는 구간이 아닙니다.");
        }
    }

    private Map<Section, Section> insertSectionBetweenSections(List<Station> stations, Section section, Station upStation, Station downStation) {
        if (stations.contains(upStation)) {
            return updateUpperConnection(section, upStation, downStation);
        }
        return updateLowerConnection(section, upStation, downStation);
    }

    private Map<Section, Section> updateUpperConnection(Section newUpSection, Station upStation, Station downStation) {
        final Section presentSection = findSectionStationAsUpStation(upStation);
        presentSection.checkInsertionPossible(newUpSection);

        final int downDistance = presentSection.subtractDistance(newUpSection);
        final Section newDownSection = new Section(lineId, downStation, presentSection.getDownStation(), downDistance);
        sections.remove(presentSection);
        sections.add(newUpSection);
        sections.add(newDownSection);
        return Collections.singletonMap(newUpSection, newDownSection);
    }

    private Section findSectionStationAsUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("구간 조회에 실패했습니다."));
    }

    private Map<Section, Section> updateLowerConnection(Section newDownSection, Station upStation, Station downStation) {
        final Section presentSection = findSectionStationAsDownStation(downStation);
        presentSection.checkInsertionPossible(newDownSection);

        final int upDistance = presentSection.subtractDistance(newDownSection);
        final Section newUpSection = new Section(lineId, presentSection.getUpStation(), upStation, upDistance);
        sections.remove(presentSection);
        sections.add(newUpSection);
        sections.add(newDownSection);
        return Collections.singletonMap(newUpSection, newDownSection);
    }

    private Section findSectionStationAsDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("구간 조회에 실패했습니다."));
    }

    public void removeSection(Station station) {
        if (checkSectionAtEdge(station)) {
            removeSectionAtEdge(station);
            return;
        }
        removeSectionInBetween(station);
    }

    public boolean checkSectionAtEdge(Station station) {
        final List<Station> stations = lineUpStations();
        return (stations.get(0).equals(station) || stations.get(stations.size() - 1).equals(station));
    }

    public Section removeSectionAtEdge(Station station) {
        final List<Station> stations = lineUpStations();
        validateStation(station, stations);
        validateLeftSection();
        if (stations.get(0).equals(station)) {
            final Section section = findSectionStationAsUpStation(station);
            sections.remove(section);
            return section;
        }
        final Section section = findSectionStationAsDownStation(station);
        sections.remove(section);
        return section;
    }

    public Map<Section, Map<Section, Section>> removeSectionInBetween(Station station) {
        final List<Station> stations = lineUpStations();
        validateStation(station, stations);
        validateLeftSection();
        final Section upperSection = findSectionStationAsDownStation(station);
        final Section lowerSection = findSectionStationAsUpStation(station);
        final int totalDistance = lowerSection.addDistance(upperSection);
        final Section newSection = new Section(lineId, upperSection.getUpStation(), lowerSection.getDownStation(), totalDistance);
        sections.add(newSection);
        sections.remove(lowerSection);
        sections.remove(upperSection);
        final Map<Section, Section> sectionsDeleted = Collections.singletonMap(lowerSection, upperSection);
        return Collections.singletonMap(newSection, sectionsDeleted);
    }

    private void validateStation(Station station, List<Station> stations) {
        if (!stations.contains(station)) {
            throw new IllegalArgumentException("구간에 속하지 않은 역입니다.");
        }
    }

    private void validateLeftSection() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("구간이 하나밖에 없어 삭제할 수 없습니다");
        }
    }
}
