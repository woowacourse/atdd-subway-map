package wooteco.subway.domain.line;

import java.util.Objects;

public class Line {
    private long id;
    private String name;
    private String color;
    private long upwardTerminalId;
    private long downwardTerminalId;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(long id, String name, String color, long upwardTerminalId, long downwardTerminalId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upwardTerminalId = upwardTerminalId;
        this.downwardTerminalId = downwardTerminalId;
    }

    public Line(String name, String color, long upwardTerminalId, long downwardTerminalId) {
        this.name = name;
        this.color = color;
        this.upwardTerminalId = upwardTerminalId;
        this.downwardTerminalId = downwardTerminalId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpwardTerminalId() {
        return upwardTerminalId;
    }

    public long getDownwardTerminalId() {
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
