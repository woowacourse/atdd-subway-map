package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineStationService {
    private List<LineStationResponse> lineStationResponses = new ArrayList<>();

    public LineStationService() {
        lineStationResponses.add(new LineStationResponse(1L, 1L, null, 10, 2));
        lineStationResponses.add(new LineStationResponse(1L, 2L, 1L, 10, 2));
        lineStationResponses.add(new LineStationResponse(1L, 3L, 2L, 10, 2));
    }

    public LineStation save(LineStation lineStation) {
        return lineStation;
    }

    public List<LineStationResponse> getLineStations(Long lineId) {
        return lineStationResponses;
    }

    public void deleteLineStationById(Long lineId, Long stationId) {
        lineStationResponses = lineStationResponses.stream()
            .filter(lineStationResponse -> !lineStationResponse.getLineId().equals(lineId)
                || !lineStationResponse.getStationId().equals(stationId))
            .collect(Collectors.toList());
    }
}
