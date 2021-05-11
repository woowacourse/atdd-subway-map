package wooteco.subway.line.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import wooteco.subway.line.Line;
import wooteco.subway.station.dto.StationResponse;

public class ReadLineDto {

    private final Long id;
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String color;
    private List<StationResponse> stationsResponses;

    public ReadLineDto(Long id, String name, String color, List<StationResponse> stationsResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationsResponses = stationsResponses;
    }

    public static ReadLineDto of(final Line line, final List<StationResponse> stationResponses) {
        return new ReadLineDto(line.getId(), line.getName(), line.getColor(), stationResponses);
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

    public List<StationResponse> getStationsResponses() {
        return stationsResponses;
    }
}
