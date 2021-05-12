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

    public static Line create(String name, String color) {
        return create(null, name, color);
    }

    public static Line create(Long id, String name, String color) {
        return new Line(id, name, color, Sections.create());
    }

    public void addSection(Section section) {
        sections.addAndThenGetModifiedAdjacent(section);
    }

    public List<Station> stations() {
        return sections.convertToSortedStations();
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
