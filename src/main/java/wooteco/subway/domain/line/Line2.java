package wooteco.subway.domain.line;

import java.util.List;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

public class Line2 {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line2(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public List<Station> getStations() {
        return sections.toSortedStations();
    }
}
