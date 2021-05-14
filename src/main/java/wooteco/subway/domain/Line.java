package wooteco.subway.domain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wooteco.subway.exception.line.InsufficientLineInformationException;

import java.util.List;
import java.util.Objects;

@Getter
public class Line {
    private final Long id;
    private String name;
    private String color;
    private Sections sections;

    private Line(Long id, String name, String color, Sections sections) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(color) || Objects.isNull(sections)) {
            throw new InsufficientLineInformationException();
        }
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line of(String name, String color) {
        return of(null, name, color, Sections.from());
    }

    public static Line of(Long id, String name, String color) {
        return of(id, name, color, Sections.from());
    }

    public static Line of(Long id, String name, String color, Sections sections) {
        return new Line(id, name, color, sections);
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

    public boolean isNotSameId(Long id) {
        return !this.id.equals(id);
    }

    public void changeInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
