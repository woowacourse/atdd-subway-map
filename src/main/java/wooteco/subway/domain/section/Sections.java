package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.station.Station;

public class Sections {

    private final List<Section> sections;

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections orderSections(Station upStation, List<Section> sections) {
        List<Section> orderedSections = new LinkedList<>();
        while (orderedSections.size() < sections.size()) {
            Section section = findSectionByUpStation(upStation, sections);
            orderedSections.add(section);
            upStation = section.getDownStation();
        }
        return new Sections(orderedSections);
    }

    private static Section findSectionByUpStation(Station station, List<Section> sections) {
        return sections.stream()
                .filter(section -> section.equalsUpStation(station))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("다음 구간 역을 찾을 수 없습니다."));
    }

    public void append(Section section) {
        if (sections.stream().anyMatch(it -> it.containsStation(section.getUpStation())) &&
                sections.stream().anyMatch(it -> it.containsStation(section.getDownStation()))) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 존재하는 구간입니다.");
        }

        Station upStation = sections.get(0).getUpStation();
        Station downStation = sections.get(sections.size() - 1).getDownStation();

        if (section.equalsDownStation(upStation)) {
            sections.add(0, section);
            return;
        }

        if (section.equalsUpStation(downStation)) {
            sections.add(section);
            return;
        }

        Optional<Section> sectionEqualsDownStation = sections.stream()
                .filter(it -> section.equalsDownStation(it.getDownStation()))
                .findAny();
        if (sectionEqualsDownStation.isPresent()) {
            Section originSection = sectionEqualsDownStation.get();
            if (originSection.isCloserThan(section)) {
                throw new IllegalArgumentException("기존 구간의 길이보다 크거나 같습니다.");
            }

            int index = sections.indexOf(originSection);
            sections.remove(index);

            sections.add(index, section);
            sections.add(index, new Section(originSection.getUpStation(), section.getUpStation(),
                    originSection.calculateDifferenceOfDistance(section)));
            return;
        }

        Optional<Section> sectionEqualsUpStation = sections.stream()
                .filter(it -> section.equalsUpStation(it.getUpStation()))
                .findAny();
        if (sectionEqualsUpStation.isPresent()) {
            Section originSection = sectionEqualsUpStation.get();
            if (originSection.isCloserThan(section)) {
                throw new IllegalArgumentException("기존 구간의 길이보다 크거나 같습니다.");
            }

            int index = sections.indexOf(originSection);
            sections.remove(index);

            sections.add(index, new Section(section.getDownStation(), originSection.getDownStation(),
                    originSection.calculateDifferenceOfDistance(section)));
            sections.add(index, section);
            return;
        }

        throw new IllegalArgumentException("상행역과 하행역이 존재하지 않는 구간입니다.");
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>(List.of(sections.get(0).getUpStation()));
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }
}
