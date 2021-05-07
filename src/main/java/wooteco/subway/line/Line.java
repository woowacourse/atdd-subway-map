package wooteco.subway.line;

import wooteco.subway.exception.LineSuffixException;
import wooteco.subway.section.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private static final String SUFFIX = "선";

    private Long id;
    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(Long id, String name, String color) { //TODO 삭제
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) { //TODO 삭제
        validateSuffix(name);
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color, String upStationId, String downStationId, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.sections = sections;
    }

    public Line(String name, String color, String upStationId, String downStationId, List<Section> sections) {
        validateSuffix(name);
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.sections = sections;
    }

    private void validateSuffix(String name) {
        if (isNotEndsWithLine(name)) {
            throw new LineSuffixException();
        }
    }

    private boolean isNotEndsWithLine(String name) {
        return !name.endsWith(SUFFIX);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
