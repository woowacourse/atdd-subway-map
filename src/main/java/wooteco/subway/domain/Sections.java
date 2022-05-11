package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections implements Iterable<Section> {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
        sortDownToUp();
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> extractStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    private void sortDownToUp() {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(sections.get(0));

        extendToDown(orderedSections, sections);
        extendToUp(orderedSections, sections);

        this.sections = orderedSections;
    }

    private void extendToUp(List<Section> orderedSections, List<Section> sections) {
        Section upTerminalSection = orderedSections.get(orderedSections.size() - 1);

        Optional<Section> newUpTerminalSection = sections.stream()
                .filter(it -> it.isAbleToLinkOnDownStation(upTerminalSection))
                .findAny();

        if (newUpTerminalSection.isPresent()) {
            orderedSections.add(newUpTerminalSection.get());
            extendToUp(orderedSections, sections);
        }
    }

    private void extendToDown(List<Section> orderedSections, List<Section> sections) {
        Section downTerminalSection = orderedSections.get(0);

        Optional<Section> newDownTerminalSection = sections.stream()
                .filter(it -> it.isAbleToLinkOnUpStation(downTerminalSection))
                .findAny();

        if (newDownTerminalSection.isPresent()) {
            orderedSections.add(0, newDownTerminalSection.get());
            extendToDown(orderedSections, sections);
        }
    }

    public void add(Section newSection) {
        validateStationsInSection(newSection);
        if (extendTerminalStationIfPossible(newSection)) {
            return;
        }
        divideProperSection(newSection);
    }

    private boolean extendTerminalStationIfPossible(Section newSection) {
        if (downTerminalStation().isAbleToLinkOnDownStation(newSection)
                || upTerminalStation().isAbleToLinkOnUpStation(newSection)) {
            sections.add(newSection);
            sortDownToUp();
            return true;
        }
        return false;
    }

    private Section downTerminalStation() {
        return sections.get(0);
    }

    private Section upTerminalStation() {
        return sections.get(sections.size() - 1);
    }

    private void divideProperSection(Section newSection) {
        Section targetSection = findTargetSection(newSection);
        sections.remove(targetSection);
        List<Section> parts = targetSection.divide(newSection);
        sections.addAll(parts);
        sortDownToUp();
    }

    private Section findTargetSection(Section newSection) {
        return sections.stream()
                .filter(it -> it.ableToDivide(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("추가하려는 section의 역 간 거리는 존재하는 section의 역 간 거리보다 작아야 합니다."));
    }

    private void validateStationsInSection(Section section) {
        boolean downStationExist = isStationExist(section.getDownStation());
        boolean upStationExist = isStationExist(section.getUpStation());

        if (downStationExist == upStationExist) {
            throw new IllegalArgumentException("추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.");
        }
    }

    private boolean isStationExist(Station station) {
        return sections.stream()
                .anyMatch(it -> it.hasStation(station));
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    public void delete(Station station) {
        validateAbleToDelete();

        List<Section> sectionsContainDeleteStation = getSectionsContainDeleteStation(station);
        sectionsContainDeleteStation.forEach(it -> sections.remove(it));
        mergeIfPossible(sectionsContainDeleteStation);
        sortDownToUp();
    }

    private void mergeIfPossible(List<Section> sectionsContainDeleteStation) {
        if (sectionsContainDeleteStation.size() == 2) {
            Section section1 = sectionsContainDeleteStation.get(0);
            Section section2 = sectionsContainDeleteStation.get(1);
            Section newSection = section1.merge(section2);
            sections.add(newSection);
        }
    }

    private List<Section> getSectionsContainDeleteStation(Station station) {
        return sections.stream()
                .filter(it -> it.hasStation(station))
                .collect(Collectors.toList());
    }

    private void validateAbleToDelete() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("해당 역을 삭제할 수 없습니다. 노선에 역은 최소 2개는 존재해야 합니다.");
        }
    }
}
