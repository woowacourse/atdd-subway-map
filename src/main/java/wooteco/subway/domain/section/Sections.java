package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import wooteco.subway.domain.station.Station;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSectionsNotEmpty(sections);
        this.sections = orderSections(sections);
    }

    private void validateSectionsNotEmpty(List<Section> sections) {
        if (sections.size() == 0) {
            throw new IllegalArgumentException("지하철구간이 필요합니다.");
        }
    }

    private List<Section> orderSections(List<Section> sections) {
        Station upStation = findUpStation(sections);
        List<Section> orderedSections = new LinkedList<>();
        while (orderedSections.size() < sections.size()) {
            Section section = findSectionByUpStation(upStation, sections);
            orderedSections.add(section);
            upStation = section.getDownStation();
        }
        return orderedSections;
    }

    private static Station findUpStation(List<Section> sections) {
        Section upSection = sections.get(0);
        while (true) {
            Section tmpSection = upSection;
            if (sections.stream().noneMatch(section -> tmpSection.equalsUpStation(section.getDownStation()))) {
                break;
            }
            upSection = sections.stream()
                    .filter(section -> tmpSection.equalsUpStation(section.getDownStation()))
                    .findAny()
                    .orElse(tmpSection);
        }
        return upSection.getUpStation();
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

    public void remove(Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("노선에 포함되어 있는 역이 아닙니다.");
        }

        if (sections.size() == 1) {
            throw new IllegalStateException("노선의 구간이 하나이므로 구간을 삭제할 수 없습니다.");
        }

        Station upStation = sections.get(0).getUpStation();
        Station downStation = sections.get(sections.size() - 1).getDownStation();

        if (upStation.equals(station)) {
            sections.remove(0);
            return;
        }

        if (downStation.equals(station)) {
            sections.remove(sections.size() - 1);
            return;
        }

        Section upSection = sections.stream()
                .filter(section -> section.equalsDownStation(station))
                .findAny()
                .orElseThrow();
        Section downSection = sections.stream()
                .filter(section -> section.equalsUpStation(station))
                .findAny()
                .orElseThrow();

        Section section = new Section(
                upSection.getUpStation(),
                downSection.getDownStation(),
                upSection.calculateSumOfDistance(downSection));

        int index = sections.indexOf(upSection);
        sections.remove(upSection);
        sections.remove(downSection);
        sections.add(index, section);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>(List.of(sections.get(0).getUpStation()));
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .map(Section::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
