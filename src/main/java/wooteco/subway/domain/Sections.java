package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        validateConnected(section);
        findUpSection(section).ifPresent(it -> updateSectionWithDownStation(section, it));
        findDownSection(section).ifPresent(it -> updateSectionWithUpStation(section, it));
        sections.add(section);
    }

    private void validateConnected(Section section) {
        List<Station> stations = findStations();
        if (stations.contains(section.getUpStation()) && stations.contains(
            section.getDownStation())) {
            throw new IllegalArgumentException("노선의 상행역과 하행역이 이미 등록되었습니다.");
        }
        if (stations.contains(section.getUpStation()) || stations.contains(
            section.getDownStation())) {
            return;
        }
        throw new IllegalArgumentException("노선의 상행역과 하행역 둘다 등록되어 있지 않습니다.");
    }

    private Optional<Section> findUpSection(Section anotherSection) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(anotherSection.getUpStation()))
            .findFirst();
    }

    private Optional<Section> findDownSection(Section anotherSection) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(anotherSection.getDownStation()))
            .findFirst();
    }

    private void updateSectionWithUpStation(Section newSection, Section registeredSection) {
        validateSectionLength(newSection, registeredSection);
        sections.add(new Section(registeredSection.getUpStation(), newSection.getUpStation(),
            registeredSection.getDistance() - newSection.getDistance()));
        sections.remove(registeredSection);
    }

    private void updateSectionWithDownStation(Section newSection, Section registeredSection) {
        validateSectionLength(newSection, registeredSection);
        sections.add(new Section(newSection.getDownStation(), registeredSection.getDownStation(),
            registeredSection.getDistance() - newSection.getDistance()));
        sections.remove(registeredSection);
    }

    private void validateSectionLength(Section newSection, Section registeredSection) {
        if (registeredSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("기존 역의 사이 길이보다 크거나 같습니다.");
        }
    }

    public List<Station> findStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Section upEndSection = findUpEndSection();
        stations.add(upEndSection.getUpStation());

        Section nextSection = upEndSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionByNextUpStation(nextSection.getDownStation());
        }
        return stations;
    }

    private Section findSectionByNextUpStation(Station station) {
        return sections.stream()
            .filter(it -> it.getUpStation().equals(station))
            .findFirst()
            .orElse(null);
    }

    private Section findUpEndSection() {
        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return sections.stream()
            .filter(it -> !downStations.contains(it.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("상행 종점이 존재하지 않습니다."));
    }

    public void removeStation(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("구간이 하나만 있을 경우 삭제할 수 없습니다.");
        }

        Optional<Section> upSection = sections.stream()
            .filter(it -> it.getUpStation().equals(station))
            .findFirst();
        Optional<Section> downSection = sections.stream()
            .filter(it -> it.getDownStation().equals(station))
            .findFirst();

        reConnection(upSection, downSection);

        upSection.ifPresent(it -> sections.remove(it));
        downSection.ifPresent(it -> sections.remove(it));
    }

    private void reConnection(Optional<Section> upSection, Optional<Section> downSection) {
        if (upSection.isPresent() && downSection.isPresent()) {
            Station newUpStation = downSection.get().getUpStation();
            Station newDownStation = upSection.get().getDownStation();
            int newDistance = upSection.get().getDistance() + downSection.get().getDistance();
            sections.add(new Section(newUpStation, newDownStation, newDistance));
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
