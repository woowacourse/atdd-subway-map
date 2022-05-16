package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Sections(Section section) {
        this.sections = Collections.singletonList(section);
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

    public boolean isNewSectionDownEnd(Section section, Sections sections) {
        Station existDownEndStation = sections.calculateDownStation();
        return section.getUpStation().equals(existDownEndStation);
    }

    public boolean isNewSectionUpEnd(Section section, Sections sections) {
        Station existUpEndStation = sections.calculateUpStation();
        return section.getDownStation().equals(existUpEndStation);
    }

    public void validateCanAddSection(Section section) {
        boolean upStationExist = sections.contains(section.getUpStation());
        boolean downStationExist = section.contains(section.getDownStation());
        if (!upStationExist && !downStationExist) {
            throw new IllegalArgumentException(
                    String.format("%s와 %s 모두 존재하지 않아 구간을 등록할 수 없습니다.", section.getUpStationName(),
                            section.getDownStationName()));
        }
        if (upStationExist && downStationExist) {
            throw new IllegalArgumentException(
                    String.format("%s와 %s 이미 모두 등록 되어있어 구간을 등록할 수 없습니다.", section.getUpStationName(),
                            section.getDownStationName()));
        }
    }

    public int size() {
        return sections.size();
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    public List<Station> getStations() {
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
