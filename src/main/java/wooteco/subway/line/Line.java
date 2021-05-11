package wooteco.subway.line;

import java.util.Objects;
import wooteco.subway.StringInput;
import wooteco.subway.line.section.Section;

public class Line {

    private Long id;
    private final StringInput name;
    private final StringInput color;
    private final Section section;

    public Line(String name, String color, Long upStationId,
        Long downStationId, int distance) {
        this(0L, name, color, upStationId, downStationId, distance);
    }

    public Line(Long id, String name, String color, Long upStationId,
        Long downStationId, int distance) {
        this.id = id;
        this.name = new StringInput(name);
        this.color = new StringInput(color);
        this.section = new Section(upStationId, downStationId, distance);
    }

    public Line(Long id, String name, String color, Section section) {
        this.id = id;
        this.name = new StringInput(name);
        this.color = new StringInput(color);
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getItem();
    }

    public String getColor() {
        return color.getItem();
    }

    public Long getUpStationId() {
        return section.getUpStationId();
    }

    public Long getDownStationId() {
        return section.getDownStationId();
    }

    public int getDistance() {
        return section.getDistance();
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
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
            && Objects.equals(color, line.color) && Objects
            .equals(section, line.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, section);
    }
}
