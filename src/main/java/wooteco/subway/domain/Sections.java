package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import wooteco.subway.exception.IllegalSectionException;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public boolean isAddableOnLine(final Section section) {
        validateSectionForAdd(section);
        if ((sections.stream().anyMatch(it -> it.isUpStationMatch(section.getUpStation()))) ||
                (sections.stream().anyMatch(it -> it.isDownStationMatch(section.getDownStation())))) {
            return true;
        }
        return false;
    }

    private void validateSectionForAdd(final Section section) {
        final List<Station> stations = orderStations();
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        if ((!stations.contains(upStation) && !stations.contains(downStation)) ||
                (stations.contains(upStation) && stations.contains(downStation))) {

            System.out.println("구간 상 하 역이 둘 다 없거나 둘 다 있거나!!!");

            throw new IllegalSectionException();
        }
    }

    public Section findOverlapSection(final Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final Section overlapSection = sections.stream()
                .filter(it -> it.isUpStationMatch(upStation) || it.isDownStationMatch(downStation))
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

    public List<Station> orderStations() {
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

    private boolean isUpTerminus(final Station upStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
                .count() == 0;
    }
}
