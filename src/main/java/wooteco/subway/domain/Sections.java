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
            Section findSection = findSectionByUpStation(section.getUpStation());
            return findSection.splitFromUpStation(section);
        }

        if (existDownStation(section.getDownStation())) {
            Section findSection = findSectionByDownStation(section.getDownStation());
            return findSection.splitFromDownStation(section);
        }

        return findSection(section);
    }

    public List<Section> deleteStation(Station station) {
        return values.stream()
            .filter(value -> value.isEqualToUpStation(station) || value.isEqualToDownStation(station))
            .collect(Collectors.toList());
    }

    public Section findSectionByUpStation(Station station) {
        return values.stream()
            .filter(value -> value.isEqualToUpStation(station))
            .findFirst()
            .orElseThrow();
    }

    public Section findSectionByDownStation(Station station) {
        return values.stream()
            .filter(value -> value.isEqualToDownStation(station))
            .findFirst()
            .orElseThrow();
    }

    private List<Section> sort(List<Section> sections) {
        List<Section> values = new ArrayList<>();
        Station firstStation = findFirstStation(sections);
        while (values.size() != sections.size()) {
            for (Section section : sections) {
                if (section.isEqualToUpStation(firstStation)) {
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
            .filter(value -> value.containsSection(section))
            .collect(Collectors.toList());
    }

    private boolean containsSection(Section section) {
        return values.stream()
            .anyMatch(value -> value.equals(section));
    }

    private boolean hasNotAnyStation(Section section) {
        return values.stream()
            .noneMatch(value -> value.containsSection(section));
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
