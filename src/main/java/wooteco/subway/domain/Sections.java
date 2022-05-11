package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
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
        }
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

    public LinkedList<Section> getSections() {
        return sections;
    }
}
