package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public Line(Long id, String name, String color, List<Station> stations) {
        this.stations = List.copyOf(stations);
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, Collections.emptyList());
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations);
    }

    private void validateName(String name) {
        Objects.requireNonNull(name, "이름은 Null 일 수 없습니다.");
        validateNameLength(name);
    }

    private void validateColor(String color) {
        Objects.requireNonNull(color, "색상은 Null 일 수 없습니다.");
        validateColorLength(color);
    }

    private void validateNameLength(String name) {
        validateLength(name.length(), 30, "이름은 1~30 자 이내여야 합니다.");
    }

    private void validateColorLength(String color) {
        validateLength(color.length(), 20, "색상은 1~20 자 이내여야 합니다.");
    }

    private void validateLength(int length, int max, String message) {
        if (length < 1 || length > max) {
            throw new IllegalArgumentException(message);
        }
    }

    public boolean hasSameId(Line line) {
        return id.equals(line.getId());
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
        return List.copyOf(stations);
    }
}
