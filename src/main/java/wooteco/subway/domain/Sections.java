package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> sections) {
        values = sort(new ArrayList<>(sections));
    }

    public boolean existUpStation(Station station) {
        return values.stream()
            .anyMatch(value -> value.getUpStation().equals(station));
    }

    public boolean existDownStation(Station station) {
        return values.stream()
            .anyMatch(value -> value.getDownStation().equals(station));
    }

    public Section findContainsUpStation(Station station) {
        return values.stream()
            .filter(value -> value.getUpStation().equals(station))
            .findFirst()
            .orElseThrow();
    }

    public Section findContainsDownStation(Station station) {
        return values.stream()
            .filter(value -> value.getDownStation().equals(station))
            .findFirst()
            .orElseThrow();
    }

    private List<Section> sort(List<Section> sections) {
        List<Section> values = new ArrayList<>();
        Station firstStation = findFirstStation(sections);
        while (values.size() != sections.size()) {
            for (Section section : sections) {
                if (section.getUpStation().equals(firstStation)) {
                    values.add(section);
                    firstStation = section.getDownStation();
                }
            }
        }

        return values;
    }

    private Station findFirstStation(List<Section> sections) {
        List<Station> upStations = createUpStations(sections);
        List<Station> downStations = createDownStations(sections);

        return upStations.stream()
            .filter(upStation -> !downStations.contains(upStation))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private List<Station> createDownStations(List<Section> sections) {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    private List<Station> createUpStations(List<Section> sections) {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section value : values) {
            stations.add(value.getUpStation());
            stations.add(value.getDownStation());
        }
        return List.copyOf(stations);
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
