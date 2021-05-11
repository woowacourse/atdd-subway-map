package wooteco.subway.dto;

import wooteco.subway.domain.Line;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    @NotBlank(message = "응답하려는 노선의 id 값이 비었습니다.")
    private Long id;
    @NotBlank(message = "응답하려는 노선의 name 값이 비었습니다.")
    private String name;
    @NotBlank(message = "응답하려는 노선의 color 값이 비었습니다.")
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
    }

    public static LineResponse of(Line line, List<StationResponse> stations) {
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
}
