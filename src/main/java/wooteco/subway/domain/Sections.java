package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new LinkedList<>(sections);
    }

    public List<Station> sortSections() {
        List<Station> stations = new LinkedList<>();
        Station station = findFirstUpStation();
        stations.add(station);

        while (hasNext(station)) {
            station = findNextStation(station);
            stations.add(station);
        }

        return stations;
    }

    private Station findFirstUpStation() {
        List<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return upStations.stream()
            .filter(station -> !downStations.contains(station))
            .findFirst()
            .orElseThrow();
    }

    private boolean hasNext(final Station station) {
        return sections.stream()
            .anyMatch(section -> section.getUpStation().equals(station));
    }

    private Station findNextStation(final Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .map(Section::getDownStation)
            .findFirst()
            .orElseThrow();
    }
}
