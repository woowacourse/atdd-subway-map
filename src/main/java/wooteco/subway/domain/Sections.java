package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
        Section originalSection = findOriginalSection(newSection);
        if (Objects.isNull(originalSection)) {
            throw new IllegalArgumentException("노선에 상행 종점과 하행 종점이 모두 존재하지 않아 구간을 추가할 수 없습니다.");
        }
    }

    private Section findOriginalSection(Section newSection) {
        Predicate<Section> hasSameUpStation = (section) -> section.hasSameUpStationWith(newSection);
        Section originalSection = findSection(hasSameUpStation);
        if (originalSection != null) {
            return originalSection;
        }
        Predicate<Section> hasSameDownStation = (section) -> section.hasSameDownStationWith(newSection);
        return findSection(hasSameDownStation);
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(getDownTermination());
        return stations;
    }

    private Station getUpTermination() {
        Section firstSection = sections.get(0);
        return firstSection.getUpStation();
    }

    private Station getDownTermination() {
        Section lastSection = sections.get(sections.size() - 1);
        return lastSection.getDownStation();
    }

    private boolean containsUpStationOf(Section newSection) {
        return sections.stream()
                .anyMatch(newSection::hasSameUpStationWith);
    }

    private Section findSection(Predicate<Section> sectionPredicate) {
        return sections.stream()
                .filter(sectionPredicate)
                .findAny()
                .orElse(null);
    }

    public LinkedList<Section> getSections() {
        return sections;
    }
}
