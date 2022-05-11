package wooteco.subway.service.dto;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.dto.line.LineResponse;
import wooteco.subway.service.dto.station.StationResponse;

public class DtoAssembler {

    public DtoAssembler() {
    }

    public static LineResponse lineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static StationResponse stationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
