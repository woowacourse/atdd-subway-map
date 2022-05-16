package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getStations() {
        Set<Station> stations = sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toSet());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public Station getLastUpStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();
        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행종점이 없습니다."));
    }

    public Station getLastDownStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();
        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행종점이 없습니다."));
    }

    public boolean hasSameUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    public boolean hasSameDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
    }

    public boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    public boolean hasStation(Station station) {
        return getStations().contains(station);
    }
}
