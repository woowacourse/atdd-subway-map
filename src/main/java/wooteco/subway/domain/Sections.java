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

    public List<Section> findUpdateSections(Section section) {
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

    public List<Section> findDeleteSections(Station station) {
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
        List<Station> upStations = getAllUpStations(sections);
        List<Station> downStations = getAllDownStations(sections);
        return fillSection(sections, findFirstStation(upStations, downStations));
    }

    private List<Section> fillSection(List<Section> sections, Station next) {
        List<Section> result = new ArrayList<>();
        while (result.size() != sections.size()) {
            next = findNextStation(sections, next, result);
        }
        return result;
    }

    private Station findNextStation(List<Section> sections, Station next, List<Section> result) {
        for (Section section : sections) {
            if (section.isEqualToUpStation(next)) {
                next = section.getDownStation();
                result.add(section);
            }
        }
        return next;
    }

    private Station findFirstStation(List<Station> upStations, List<Station> downStations) {
        return upStations.stream()
            .filter(upStation -> !downStations.contains(upStation))
            .findFirst()
            .orElseThrow(() ->  new IllegalArgumentException("첫번째 역이 존재하지 않습니다."));
    }

    private List<Station> getAllUpStations(List<Section> sections) {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private List<Station> getAllDownStations(List<Section> sections) {
        return sections.stream()
            .map(Section::getDownStation)
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
