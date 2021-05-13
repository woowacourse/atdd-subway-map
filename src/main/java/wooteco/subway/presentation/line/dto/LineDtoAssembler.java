package wooteco.subway.presentation.line.dto;

import org.springframework.stereotype.Component;
import wooteco.subway.application.station.StationService;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;
import wooteco.subway.presentation.station.dto.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class LineDtoAssembler {

    private final StationService stationService;

    public LineDtoAssembler(StationService stationService) {
        this.stationService = stationService;
    }

    public LineResponse line(Line line) {
        return new LineResponse(
                line.getLineId(),
                line.getLineName(),
                line.getLineColor(),
                getStationResponses(line)
        );
    }

    public StationResponse station(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    private List<StationResponse> getStationResponses(Line line) {
        return line.getStationIds().stream()
                .map(stationService::findById)
                .map(this::station)
                .collect(toList());
    }

}
