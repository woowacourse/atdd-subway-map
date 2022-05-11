package wooteco.subway.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(final Line line, final Section section) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                List.of(StationResponse.from(section.getUpStation()),
                        StationResponse.from(section.getDownStation())));
    }

    public static LineResponse from(final Line line) {
        List<StationResponse> stationResponses = line.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public static LineResponse of(final Line line, final List<Section> sections) {

        List<StationResponse> stations = new ArrayList<>();

        // TODO: 2022/05/11 구간 사이에 있는 역이 두번 들어감
        for (Section section : sections) {
            stations.add(StationResponse.from(section.getUpStation()));
            stations.add(StationResponse.from(section.getDownStation()));
        }
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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
        final LineResponse that = (LineResponse) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
