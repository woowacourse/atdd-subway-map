package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public static Line of(String name, String color) {
        return new Line(null, name, color, Sections.from());
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> stations() {
        return sections.asStations();
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameColor(String color) {
        return this.color.equals(color);
    }

    public boolean isSameId(Long lineId) {
        return id.equals(lineId);
    }

    public void insertSections(Sections sections) {
        this.sections = sections;
    }
}
