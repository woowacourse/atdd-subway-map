package wooteco.subway.controller.response;

import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.LineWithStationsDto;

import java.util.List;

// 특정 노선 조회 시 사용되는 응답
public class LineRetrieveResponse {
    private Long id;
    private String color;
    private String name;
    private List<StationResponse> stations;

    public LineRetrieveResponse() {
    }

    public LineRetrieveResponse(Line line) {
        this(line.getId(), line.getColor(), line.getName());
    }

    public LineRetrieveResponse(LineDto lineDto) {
        this(lineDto.getId(), lineDto.getColor(), lineDto.getName());
    }

    public LineRetrieveResponse(LineWithStationsDto lineWithStations) {
        this(lineWithStations.getId(), lineWithStations.getColor(),
                lineWithStations.getName(), lineWithStations.getStations());
    }

    public LineRetrieveResponse(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public LineRetrieveResponse(Long id, String color, String name, List<StationResponse> stations) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.stations = stations;
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
