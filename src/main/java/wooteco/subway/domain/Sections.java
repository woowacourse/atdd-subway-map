package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean containsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    public Station calculateUpStation() {
        List<Station> upStations = getUpperStations();
        List<Station> downStations = getDownerStations();

        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행역이 존재하지 않습니다."));
    }

    public Station calculateDownStation() {
        List<Station> upStations = getUpperStations();
        List<Station> downStations = getDownerStations();

        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("하행역이 존재하지 않습니다."));
    }

    public List<Station> getDownerStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Station> getUpperStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        sections.add(section);
    }

    public Section findSectionWithUpperStation(Station upStation) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행 쪽의 역이 존재하지 않습니다."));
    }

    public Section changeSectionWithNewSections(Section sectionWithUpperStation, List<Section> newAddedSections) {
        sections.remove(sectionWithUpperStation);
        sections.addAll(newAddedSections);
        return sectionWithUpperStation;
    }

    public Section findSectionWithLowerStation(Station downStation) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("하행 쪽의 역이 존재하지 않습니다."));
    }

    public int size() {
        return sections.size();
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public ArrayList<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();

        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            stations.add(upStation);
            stations.add(downStation);
        }
        return new ArrayList<>(stations);
    }
}
