package wooteco.subway.domain.line;

import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upwardTerminalId;
    private Long downwardTerminalId;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Long upwardTerminalId, Long downwardTerminalId) {
        this.name = name;
        this.color = color;
        this.upwardTerminalId = upwardTerminalId;
        this.downwardTerminalId = downwardTerminalId;
    }

    public Line(Long id, String name, String color, Long upwardTerminalId, Long downwardTerminalId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upwardTerminalId = upwardTerminalId;
        this.downwardTerminalId = downwardTerminalId;
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

    public Long getUpwardTerminalId() {
        return upwardTerminalId;
    }

    public Long getDownwardTerminalId() {
        return downwardTerminalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
