package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Sections {
    private final LinkedList<Section> sections;

    public Sections(LinkedList<Section> sections) {
        this.sections = sections;
    }

    public Sections(List<Section> rawSections) {
        this(new LinkedList<>(rawSections));
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (section.isDownStation(getUpTermination())) {
            sections.addFirst(section);
            return;
        }
        if (section.isUpStation(getDownTermination())) {
            sections.addLast(section);
            return;
        }
        splitAndAdd(section);
    }

    private void splitAndAdd(Section newSection) {
        Predicate<Section> hasSameUpStation = (section) -> section.hasSameUpStationWith(newSection);
        Section originalSection = findSection(hasSameUpStation);
        if (originalSection != null) {
            Section splitSection = originalSection.splitRightBy(newSection);
            sections.add(sections.indexOf(originalSection), newSection);
            sections.add(sections.indexOf(originalSection), splitSection);
            sections.remove(originalSection);
            return;
        }
        Predicate<Section> hasSameDownStation = (section) -> section.hasSameDownStationWith(newSection);
        originalSection = findSection(hasSameDownStation);
        if (originalSection != null) {
            Section splitSection = originalSection.splitLeftBy(newSection);
            sections.add(sections.indexOf(originalSection), splitSection);
            sections.add(sections.indexOf(originalSection), newSection);
            sections.remove(originalSection);
            return;
        }
        throw new IllegalArgumentException("노선에 상행 종점과 하행 종점이 모두 존재하지 않아 구간을 추가할 수 없습니다.");
    }

    public Section delete(Station station) {
        checkSize();
        if (station.equals(getUpTermination())) {
            return sections.removeFirst();
        }
        if (station.equals(getDownTermination())) {
            return sections.removeLast();
        }
        return mergeAndDelete(station);
    }

    private Section mergeAndDelete(Station station) {
        Section originalSection = findSection((section) -> section.isDownStation(station));
        if (originalSection == null) {
            throw new IllegalArgumentException("해당 역은 노선에 존재하지 않습니다.");
        }
        int mergingIndex = sections.indexOf(originalSection) + 1;
        Section mergedSection = originalSection.mergeWith(sections.get(mergingIndex));
        sections.add(mergingIndex, mergedSection);
        sections.remove(originalSection);
        return sections.remove(mergingIndex);
    }

    private void checkSize() {
        if (sections.size() <= 1) {
            throw new IllegalStateException("노선에 구간이 하나 뿐일 때에는 삭제할 수 없습니다.");
        }
    }

    private Section findSection(Predicate<Section> sectionPredicate) {
        return sections.stream()
                .filter(sectionPredicate)
                .findAny()
                .orElse(null);
    }

    private Station getUpTermination() {
        Section firstSection = sections.get(0);
        return firstSection.getUpStation();
    }

    private Station getDownTermination() {
        Section lastSection = sections.get(sections.size() - 1);
        return lastSection.getDownStation();
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(getDownTermination());
        return stations;
    }

    public LinkedList<Section> getSections() {
        return sections;
    }
}
