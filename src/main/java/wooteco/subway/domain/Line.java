package wooteco.subway.domain;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.lang.NonNull;

public class Line {

    private final Long id;
    @NonNull
    private final String name;
    @NonNull
    private final String color;
    @NonNull
    private final Sections sections;

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, Sections sections) {
        this(null, name, color, sections);
    }

    public Station firstStation() {
        return sections.firstStation();
    }

    public Station lastStation() {
        return sections.lastStation();
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

    public Set<Section> sections() {
        return sections.values();
    }

    public List<Section> sectionsWithStation(Station station) {
        return sections.sectionsWithStation(station);
    }

    public int sectionCount() {
        return sections.count();
    }

    public boolean hasNotStation(Station station) {
        return sections.hasNotStation(station);
    }

    public List<Station> path() {
        return sections.path();
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
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
