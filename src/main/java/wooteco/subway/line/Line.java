package wooteco.subway.line;

import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(Long id, String name, String color) {
        validateLineField(name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public List<Station> stations() {
        return sections.sortedStations();
    }

    private void validateLineField(String name, String color) {
        validateName(name);
        validateColor(color);
    }

    private void validateName(String name) {
        if (name == null || name.trim().length() <= 0) {
            throw new LineException("이름 값이 존재해야 합니다.");
        }
    }

    private void validateColor(String color) {
        if (color == null || color.trim().length() <= 0) {
            throw new LineException("색상 값이 존재해야 합니다.");
        }
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

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void setSections(Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
