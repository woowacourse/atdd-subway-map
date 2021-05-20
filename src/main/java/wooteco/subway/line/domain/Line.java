package wooteco.subway.line.domain;

import wooteco.subway.line.domain.rule.FindSectionStrategy;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private Sections sections;

    public Line(String name, String color) {
        this(0L, name, color, new ArrayList<>());
    }

    public Line(String name, String color, List<Section> sections) {
        this(0L, name, color, sections);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, Section section) {
        this(id, name, color, Arrays.asList(section));
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color, new Sections(sections));
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public void initSections(List<Section> sections) {
        this.sections = new Sections(sections);
    }

    public List<Station> stations() {
        return sections.sortedStations();
    }

    public List<Long> stationIds() {
        return sections.stationIds();
    }

    public Station registeredStation(Section anotherSection) {
        return sections.registeredStation(anotherSection);
    }

    public Section findSectionWithUpStation(Station duplicatedStation) {
        return sections.findSectionWithUpStation(duplicatedStation);
    }

    public Section findSectionWithDownStation(Station duplicatedStation) {
        return sections.findSectionWithDownStation(duplicatedStation);
    }

    public boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    public List<Section> sectionsWhichHasStation(Station station) {
        List<Section> sectionsWhichHasStation = new ArrayList<>();
        if (!findSectionWithDownStation(station).isEmpty()) {
            sectionsWhichHasStation.add(findSectionWithDownStation(station));
        }
        if (!findSectionWithUpStation(station).isEmpty()) {
            sectionsWhichHasStation.add(findSectionWithUpStation(station));
        }
        return sectionsWhichHasStation;
    }

    public Section findSectionWithStation(Station targetStation, List<FindSectionStrategy> findSectionStrategies) {
        return sections.findSectionWithStation(targetStation, findSectionStrategies);
    }
}
