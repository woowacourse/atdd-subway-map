package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sort(new ArrayList<>(sections));
    }

    public List<Section> findUpdateSections(Section section) {
        validateIsExist(section);
        return findAddedSection(section).split(section);
    }

    public List<Section> findDeleteSections(Station station) {
        return sections.stream()
            .filter(value -> value.isEqualToUpOrDownStation(station))
            .collect(Collectors.toList());
    }

    public Section findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(value -> value.isEqualToUpStation(station))
            .findFirst()
            .orElseThrow();
    }

    public Section findSectionByDownStation(Station station) {
        return sections.stream()
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

    private void validateIsExist(Section section) {
        if (containsSection(section)) {
            throw new IllegalArgumentException("기존에 존재하는 구간입니다.");
        }
    }

    private Section findAddedSection(Section section) {
        return sections.stream()
            .filter(it -> it.isEqualToUpStation(section.getUpStation()) || it.isEqualToDownStation(section.getDownStation()))
            .findFirst()
            .orElseGet(() -> isAddableFirstOrEndSection(section));
    }

    private Section isAddableFirstOrEndSection(Section section) {
        return sections.stream()
            .filter(it -> it.isEqualToUpStation(section.getDownStation()) || it.isEqualToDownStation(section.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("생성할 수 없는 구간입니다."));
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

    private boolean containsSection(Section section) {
        return sections.stream()
            .anyMatch(value -> value.equals(section));
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section value : sections) {
            stations.add(value.getUpStation());
            stations.add(value.getDownStation());
        }
        return List.copyOf(stations);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
