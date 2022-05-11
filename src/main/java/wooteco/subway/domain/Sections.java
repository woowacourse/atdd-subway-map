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

    public List<Section> add(Section section) {
        if (containsSection(section)) {
            throw new IllegalArgumentException("기존에 존재하는 구간입니다.");
        }

        if (hasNotAnyStation(section)) {
            throw new IllegalArgumentException("생성할 수 없는 구간입니다.");
        }

        if (existUpStation(section.getUpStation())) {
            Section findSection = findContainsUpStation(section.getUpStation());
            return findSection.splitFromUpStation(section);
        }

        if (existDownStation(section.getDownStation())) {
            Section findSection = findContainsDownStation(section.getDownStation());
            return findSection.splitFromDownStation(section);
        }

        return findSection(section);
    }

    public List<Section> deleteStation(Station station) {
        return values.stream()
            .filter(value -> value.getUpStation().equals(station) || value.getDownStation().equals(station))
            .collect(Collectors.toList());
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

    private boolean existUpStation(Station station) {
        return values.stream()
            .anyMatch(value -> value.isEqualToUpStation(station));
    }

    private boolean existDownStation(Station station) {
        return values.stream()
            .anyMatch(value -> value.isEqualToDownStation(station));
    }

    private List<Section> findSection(Section section) {
        return values.stream()
            .filter(value -> value.hasStation(section.getUpStation()) || value.hasStation(section.getDownStation()))
            .collect(Collectors.toList());
    }

    private boolean hasNotAnyStation(Section section) {
        return values.stream()
            .noneMatch(value -> value.hasStation(section.getUpStation()) || value.hasStation(section.getDownStation()));
    }

    private boolean containsSection(Section section) {
        return values.stream()
            .anyMatch(value -> value.equals(section));
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
