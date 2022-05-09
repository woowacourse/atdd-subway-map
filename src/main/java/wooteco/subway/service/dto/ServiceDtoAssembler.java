package wooteco.subway.service.dto;

import java.util.Collections;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.dto.line.LineResponseDto;
import wooteco.subway.service.dto.station.StationResponseDto;

public class ServiceDtoAssembler {

    private ServiceDtoAssembler() {
    }

    public static StationResponseDto stationResponseDto(Station station) {
        return new StationResponseDto(station.getId(), station.getName());
    }

    public static LineResponseDto lineResponseDto(Line line) {
        return new LineResponseDto(line.getId(), line.getName(), line.getColor(), Collections.emptyList());
    }
}
