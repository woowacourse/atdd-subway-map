package wooteco.subway.section;

import wooteco.subway.station.Station;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private Long lineId;

    private List<Section> sections;

    public Sections() {
    }

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

    public boolean checkSameLineId(Long id) {
        return this.lineId.equals(id);
    }

    public List<Station> lineUpStations() {
        final Map<Station, Station> sectionConnection = generateStationConnection();

        Station station = findUpEndStation();
        List<Station> lineUpStations = new ArrayList<>();
        while (sectionConnection.containsKey(station)) {
            lineUpStations.add(station);
            station = sectionConnection.get(station);
        }
        lineUpStations.add(station);

        return lineUpStations;
    }

    private Map<Station, Station> generateStationConnection() {
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
        validateConnection(stations, section);

        if (stations.get(0).equals(section.getDownStation()) ||
                stations.get(stations.size() - 1).equals(section.getUpStation())) {
            sections.add(section);
            return true;
        }
        return false;
    }

    private void validateConnection(List<Station> stationsInSection, Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();

        if ((stationsInSection.contains(upStation) && stationsInSection.contains(downStation)) ||
                (!stationsInSection.contains(upStation) && !stationsInSection.contains(downStation))) {
            throw new IllegalArgumentException("연결될 수 있는 구간이 아닙니다.");
        }
    }

    public Map<Section, Section> insertSectionInBetween(Section section) {
        final List<Station> stations = lineUpStations();
        validateConnection(stations, section);
        if (stations.contains(section.getUpStation())) {
            return insertUpperSectionInBetween(section);
        }
        return insertLowerSectionInBetween(section);
    }

    private Map<Section, Section> insertUpperSectionInBetween(Section newUpSection) {
        final Section presentSection = findSectionHoldingAsUpStation(newUpSection.getUpStation());
        presentSection.checkInsertionPossible(newUpSection);

        final int downDistance = presentSection.subtractDistance(newUpSection);
        final Section newDownSection = new Section(lineId, newUpSection.getDownStation(), presentSection.getDownStation(), downDistance);
        sections.remove(presentSection);
        sections.add(newUpSection);
        sections.add(newDownSection);
        return Collections.singletonMap(newUpSection, newDownSection);
    }

    private Section findSectionHoldingAsUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("구간 조회에 실패했습니다."));
    }

    private Map<Section, Section> insertLowerSectionInBetween(Section newDownSection) {
        final Section presentSection = findSectionHoldingAsDownStation(newDownSection.getDownStation());
        presentSection.checkInsertionPossible(newDownSection);

        final int upDistance = presentSection.subtractDistance(newDownSection);
        final Section newUpSection = new Section(lineId, presentSection.getUpStation(), newDownSection.getUpStation(), upDistance);
        sections.remove(presentSection);
        sections.add(newUpSection);
        sections.add(newDownSection);
        return Collections.singletonMap(newUpSection, newDownSection);
    }

    private Section findSectionHoldingAsDownStation(Station downStation) {
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
            final Section section = findSectionHoldingAsUpStation(station);
            sections.remove(section);
            return section;
        }

        final Section section = findSectionHoldingAsDownStation(station);
        sections.remove(section);
        return section;
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

    public Map<Section, Map<Section, Section>> removeSectionInBetween(Station station) {
        final List<Station> stations = lineUpStations();
        validateStation(station, stations);
        validateLeftSection();

        final Section upperSection = findSectionHoldingAsDownStation(station);
        final Section lowerSection = findSectionHoldingAsUpStation(station);
        final int totalDistance = lowerSection.addDistance(upperSection);
        final Section newSection = new Section(lineId, upperSection.getUpStation(), lowerSection.getDownStation(), totalDistance);

        sections.remove(upperSection);
        sections.remove(lowerSection);
        final Map<Section, Section> sectionsDeleted = Collections.singletonMap(lowerSection, upperSection);

        sections.add(newSection);
        return Collections.singletonMap(newSection, sectionsDeleted);
    }
}
