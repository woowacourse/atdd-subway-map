package wooteco.subway.line;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.Stations;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;
    private final Stations stations;

    public Line(String name, String color) {
        this(null, name, color, Collections.emptyList(), Collections.emptyMap());
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, Collections.emptyList(), Collections.emptyMap());
    }

    public Line(Long id, String name, String color, List<Section> sectionGroup, Map<Long, Station> stationGroup) {
        this(id, name, color, new Sections(sectionGroup), new Stations(stationGroup));
    }

    public Line(final Long id, final String name, final String color, final Sections sections,
        final Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public Stations getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects
            .equals(color, line.color) && Objects.equals(stations, line.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations);
    }
}
