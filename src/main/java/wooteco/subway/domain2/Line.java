package wooteco.subway.domain2;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredStationEntity;
import wooteco.subway.entity.StationEntity;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    private Line(Long id,
                 String name,
                 String color,
                 List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static Line of(LineEntity entity, List<Station> stations) {
        return new Line(entity.getId(), entity.getName(), entity.getColor(), stations);
    }

    public static Line of(LineEntity entity, StationEntity upStation, StationEntity downStation) {
        List<Station> stations = List.of(upStation.toDomain(), downStation.toDomain());
        return new Line(entity.getId(), entity.getName(), entity.getColor(), stations);
    }

    public static Line of(List<RegisteredStationEntity> entities) {
        LineEntity line = entities.get(0).getLineEntity();
        List<Station> stations = entities.stream()
                .map(RegisteredStationEntity::getStationEntity)
                .map(StationEntity::toDomain)
                .collect(Collectors.toList());
        return new Line(line.getId(), line.getName(), line.getColor(), stations);
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
        return stations;
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
        return Objects.equals(id, line.id)
                && Objects.equals(name, line.name)
                && Objects.equals(color, line.color)
                && Objects.equals(stations, line.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
