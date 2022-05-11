package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import wooteco.subway.exception.IllegalSectionException;

public class SectionsOnTheLine {

    public static final int SINGLE_COUNT = 1;

    private final List<Section> sections;

    public SectionsOnTheLine(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public boolean isAddableOnTheLine(final Section section) {
        validateSectionForAdd(section);
        return doMatchedUpStationExist(section) || doMatchedDownStationExist(section);
    }

    private boolean doMatchedUpStationExist(final Section sectionToAdd) {
        return sections.stream().anyMatch(section -> section.isUpStation(sectionToAdd.getUpStation()));
    }

    private boolean doMatchedDownStationExist(final Section sectionToAdd) {
        return sections.stream().anyMatch(section -> section.isDownStation(sectionToAdd.getDownStation()));
    }

    private void validateSectionForAdd(final Section section) {
        final List<Station> stations = lineUpStations();
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        if (doNotAllStationExist(stations, upStation, downStation) || doAllStationExist(stations, upStation,
                downStation)) {
            throw new IllegalSectionException();
        }
    }

    private boolean doNotAllStationExist(final List<Station> stations, final Station upStation,
                                         final Station downStation) {
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    private boolean doAllStationExist(final List<Station> stations, final Station upStation,
                                      final Station downStation) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public Section findOverlapSection(final Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final Section overlapSection = sections.stream()
                .filter(it -> it.isUpStation(upStation) || it.isDownStation(downStation))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 겹치는 구간을 찾을 수 없습니다."));
        validateDistance(section, overlapSection);
        return overlapSection;
    }

    private void validateDistance(final Section section, final Section overlapSection) {
        if (overlapSection.getDistance() <= section.getDistance()) {
            throw new IllegalSectionException();
        }
    }

    public List<Station> lineUpStations() {
        final List<Station> stations = new ArrayList<>();
        final Station upTerminus = findUpTerminus();
        final Map<Station, Station> map = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        collectStations(map, stations, upTerminus);
        return stations;
    }

    private void collectStations(final Map<Station, Station> map,
                                 final List<Station> stations,
                                 final Station upTerminus) {
        stations.add(upTerminus);
        final Station valueStation = map.get(upTerminus);
        if (!map.containsKey(valueStation)) {
            stations.add(valueStation);
            return;
        }
        collectStations(map, stations, valueStation);
    }

    private Station findUpTerminus() {
        return sections.stream()
                .filter(section -> isUpTerminus(section.getUpStation()))
                .map(Section::getUpStation)
                .findFirst().orElseThrow(() -> new NoSuchElementException("[ERROR] 상행 종점을 찾을 수 없습니다."));
    }

    private boolean isUpTerminus(final Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .count() == 0;
    }

    private boolean isDownTerminus(final Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .count() == 0;
    }

    public boolean isTerminus(final Station station) {
        return isUpTerminus(station) || isDownTerminus(station);
    }

    public boolean contains(final Station station) {
        return lineUpStations().contains(station);
    }

    public Section findByDownStation(final Station station) {
        return sections.stream()
                .filter(section -> section.isDownStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 하행역이 일치하는 역을 찾을 수 없습니다."));
    }

    public Section findByUpStation(final Station station) {
        return sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 상행역이 일치하는 역을 찾을 수 없습니다."));
    }

    public boolean hasSingleSection() {
        return sections.size() == SINGLE_COUNT;
    }
}
