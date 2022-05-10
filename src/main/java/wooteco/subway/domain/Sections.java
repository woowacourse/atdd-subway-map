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
        final List<Station> stations = orderStations();
        validateSectionForAdd(section, stations);
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        if ((stations.contains(upStation) && !stations.contains(downStation)) ||
                (!stations.contains(upStation) && stations.contains(downStation))) {
            return true;
        }
        return false;
    }

    private void validateSectionForAdd(final Section section, final List<Station> stations) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        if ((!stations.contains(upStation) && !stations.contains(downStation)) ||
                (stations.contains(upStation) && stations.contains(downStation))) {
            throw new IllegalSectionException();
        }
    }

    public Section findOverlapSection(final Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final Section overlapSection = sections.stream()
                .filter(it -> it.isContain(upStation) || it.isContain(downStation))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 겹치는 구간을 찾을 수 없습니다."));
        if (overlapSection.getDistance() <= section.getDistance()) {
            throw new IllegalSectionException();
        }
        return overlapSection;
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
        final Station nextStation = map.get(upTerminus);
        if (map.containsKey(nextStation)) {
            collectStations(map, stations, nextStation);
        }
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
