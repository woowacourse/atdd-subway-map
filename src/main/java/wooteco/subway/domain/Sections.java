package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSize(sections);
        this.sections = new ArrayList<>(sections);
    }

    public Sections(Section section) {
        this(List.of(section));
    }

    private void validateSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalStateException("구간은 하나 이상 존재해야 합니다.");
        }
    }

    public void insert(Section section) {
        List<Section> connectableSections = findConnectableSections(section); // A-B를 삽입할 경우 A 혹은 B가 포함된 모든 Section을 가져옴
        validateIncludingStations(section, connectableSections); // A-B를 삽입하려는데 A -> B 로 가는 구간이 이미 존재할 경우 예외 발생
        Optional<Section> optionalSameUpOrDownStationSection = connectableSections.stream()
                .filter(sec -> sec.isSameUpOrDownStation(section))
                .findFirst();
        if (optionalSameUpOrDownStationSection.isPresent()) {
            Section originSection = optionalSameUpOrDownStationSection.get();
            validateDistance(originSection, section);
            addMiddleSection(originSection, section);
            return;
        }
        sections.add(section);
    }

    private void addMiddleSection(Section originSection, Section insertSection) {
        sections.remove(originSection);
        sections.add(insertSection);
        sections.add(originSection.slice(insertSection));
    }

    private void validateDistance(Section originSection, Section insertSection) {
        if (originSection.isShortAndEqualDistanceThan(insertSection)) {
            throw new IllegalArgumentException("역 사이에 구간을 등록할 경우 기존 역 구간 길이보다 짧아야 합니다.");
        }
    }

    private void validateIncludingStations(Section section, List<Section> connectableSections) {
        boolean haveSameUpStation = connectableSections.stream().anyMatch(sec -> sec.haveUpStation(section));
        boolean haveSameDownStation = connectableSections.stream().anyMatch(sec -> sec.haveDownStation(section));
        if (haveSameUpStation && haveSameDownStation) {
            throw new IllegalArgumentException("해당 구간은 기존 노선에 이미 등록되어있습니다.");
        }
    }

    private List<Section> findConnectableSections(Section section) {
        List<Section> connectableSections = sections.stream()
                .filter(sec -> sec.haveAnyStation(section))
                .collect(Collectors.toList());
        validateNoSameStationsInSection(connectableSections);
        return connectableSections;
    }

    private void validateNoSameStationsInSection(List<Section> connectableSections) {
        if (connectableSections.isEmpty()) {
            throw new IllegalArgumentException("상행역 또는 하행역이 노선에 포함되어 있어야합니다.");
        }
    }

    public void delete(Station station) {
        List<Section> removableSection = findRemovableSections(station);
        validateIncludingStation(removableSection);
        validateSectionSize();

        if (removableSection.size() == 2) {
            removeInMiddle(station, removableSection);
        }
        sections.remove(removableSection.get(0));
    }

    public List<Section> getDifferentList(Sections otherSections) {
        Set<String> set = new HashSet<>();

        List<Section> thisSections = new ArrayList<>(this.sections);
        thisSections.removeAll(otherSections.sections);
        return thisSections;
    }

    private List<Section> findRemovableSections(Station station) {
        return sections.stream()
                .filter(sec -> sec.haveStation(station))
                .collect(Collectors.toList());
    }

    private void removeInMiddle(Station station, List<Section> removableSection) {
        Section section1 = removableSection.get(0);
        Section section2 = removableSection.get(1);

        sections.add(section1.combine(section2, station));
        sections.remove(section1);
        sections.remove(section2);
    }

    private void validateIncludingStation(List<Section> removableSection) {
        if (removableSection.isEmpty()) {
            throw new IllegalArgumentException("지우려는 역이 노선에 포함되어 있어야합니다.");
        }
    }

    private void validateSectionSize() {
        if (sections.size() == 1) {
            throw new IllegalStateException("구간이 오직 하나인 노선에서 역을 제거할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
