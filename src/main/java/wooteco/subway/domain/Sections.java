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

    public boolean isAddableOnMiddle(final Section section) {
        final List<Station> stations = getStations();
        validateAddableSection(section, stations);
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        if ((stations.contains(upStation) && !stations.contains(downStation)) ||
                (!stations.contains(upStation) && stations.contains(downStation))) {
            return true;
        }
        return false;
    }

    private void validateAddableSection(final Section section, final List<Station> stations) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new IllegalSectionException();
        }
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new IllegalSectionException();
        }
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        final Station upTerminus = findUpTerminus();
        final Map<Station, Station> map = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        collectStations(map, stations, upTerminus);
        return stations;
    }

    private void collectStations(final Map<Station, Station> map, final List<Station> stations, final Station station) {
        stations.add(station);
        final Station nextStation = map.get(station);
        if (map.containsKey(nextStation)) {
            collectStations(map, stations, nextStation);
        }
    }

    private Station findUpTerminus() {
        return sections.stream()
                .filter(section -> isUpTerminus(section.getUpStation()))
                .map(Section::getUpStation)
                .findFirst().orElseThrow(() -> new NoSuchElementException("[ERROR] 상행종점을 찾을 수 없습니다."));
    }

    private boolean isUpTerminus(final Station upStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
                .count() == 0;
    }
}
