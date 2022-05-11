package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private List<Section> sections;

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(Section section) {
        return new Sections(new LinkedList<>(List.of(section)));
    }

    public static Sections of(List<Section> sections) {
        Map<Station, Station> stations = sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Station upStation = findUpStation(stations);
        List<Section> newSections = getSortedSections(sections, stations, upStation);

        return new Sections(newSections);
    }

    private static List<Section> getSortedSections(List<Section> sections, Map<Station, Station> stations,
        Station upStation) {
        List<Section> newSections = new LinkedList<>();
        while (stations.containsKey(upStation)) {
            final Station station = upStation;
            newSections.add(findSectionByUpStation(sections, station));
            upStation = stations.get(upStation);
        }
        return newSections;
    }

    private static Section findSectionByUpStation(List<Section> sections, Station station) {
        return sections.stream()
            .filter(section -> section.isUpStation(station))
            .findFirst()
            .get();
    }

    private static Station findUpStation(Map<Station, Station> stations) {
        return stations.keySet().stream()
            .filter(station -> !stations.containsValue(station))
            .findFirst()
            .get();
    }

    public void insert(Section section) {
        LinkedList<Section> flexibleSections = new LinkedList<>(this.sections);
        for (int i = 0; i < flexibleSections.size(); i++) {
            Section sectionInLine = flexibleSections.get(i);
            if (insertSection(section, flexibleSections, i, sectionInLine))
                return;
        }

        if (flexibleSections.size() == sections.size()) {
            insertSectionSide(section, flexibleSections);
        }
    }

    private boolean insertSection(Section section, LinkedList<Section> flexibleSections, int i, Section sectionInLine) {
        if (canInsertUpStation(section, sectionInLine)) {
            sectionInLine.updateUpStation(section.getDownStation(), section.getDistance());
            flexibleSections.add(i, section);
            sections = flexibleSections;
            return true;
        }
        if (canInsertDownStation(section, sectionInLine)) {
            sectionInLine.updateDownStation(section.getUpStation(), section.getDistance());
            flexibleSections.add(i + 1, section);
            sections = flexibleSections;
            return true;
        }
        return false;
    }

    private void insertSectionSide(Section section, LinkedList<Section> flexibleSections) {
        Section lastSection = sections.get(sections.size() - 1);
        if (lastSection.isDownStation(section.getUpStation())) {
            flexibleSections.addLast(section);
            sections = flexibleSections;
            return;
        }

        Section firstSection = sections.get(0);
        if (firstSection.isUpStation(section.getDownStation())) {
            flexibleSections.addFirst(section);
            sections = flexibleSections;
        }
    }

    private boolean canInsertDownStation(Section section, Section sectionInLine) {
        return sectionInLine.isDownStation(section.getDownStation())
            && sectionInLine.isLongerThan(section.getDistance());
    }

    private boolean canInsertUpStation(Section section, Section sectionInLine) {
        return sectionInLine.isUpStation(section.getUpStation())
            && sectionInLine.isLongerThan(section.getDistance());
    }

    public List<Section> getSections() {
        return new LinkedList<>(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream()
            .distinct()
            .collect(Collectors.toList());
    }
}
