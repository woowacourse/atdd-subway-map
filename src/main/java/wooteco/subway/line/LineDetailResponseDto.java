package wooteco.subway.line;

import java.util.List;
import wooteco.subway.station.Station;

public class LineDetailResponseDto {

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public LineDetailResponseDto(Long id, String name, String color,
        List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }
}
