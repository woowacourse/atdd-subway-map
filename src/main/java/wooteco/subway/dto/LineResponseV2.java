package wooteco.subway.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class LineResponseV2 {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponseV2(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponseV2 of(final Line line, final Section section) {
        return new LineResponseV2(line.getId(), line.getName(), line.getColor(),
                List.of(StationResponse.from(section.getUpStation()),
                        StationResponse.from(section.getDownStation())));
    }

    public static LineResponseV2 of(final Line line, final List<Section> sections) {

        List<StationResponse> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(StationResponse.from(section.getUpStation()));
            stations.add(StationResponse.from(section.getDownStation()));
        }
        return new LineResponseV2(line.getId(), line.getName(), line.getColor(), stations);
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

    public List<StationResponse> getStations() {
        return stations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LineResponseV2 that = (LineResponseV2) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
