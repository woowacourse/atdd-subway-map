package wooteco.subway.service.dto;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.dto.line.LineResponse;
import wooteco.subway.service.dto.station.StationResponse;

public class DtoAssembler {

    public DtoAssembler() {
    }

    public static LineResponse lineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses(line.getStations()));
    }

    public static List<LineResponse> lineResponses(List<Line> lines) {
        return lines.stream()
                .map(DtoAssembler::lineResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public static StationResponse stationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> stationResponses(List<Station> stations) {
        return stations.stream()
                .map(DtoAssembler::stationResponse)
                .collect(Collectors.toUnmodifiableList());
    }
}
