package wooteco.subway.entity;

import java.util.ArrayList;
import java.util.Objects;

public class LineEntity {

    private Long id;
    private final String name;
    private final String color;
    private final ArrayList<StationEntity> stationEntities;

    public LineEntity(String name, String color) {
        this.name = name;
        this.color = color;
        this.stationEntities = null;
    }

    public LineEntity(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationEntities = null;
    }

    public LineEntity(Long id, String name, String color, ArrayList<StationEntity> stationEntities) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationEntities = stationEntities;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineEntity lineEntity = (LineEntity) o;
        return Objects.equals(name, lineEntity.name) && Objects.equals(color, lineEntity.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
