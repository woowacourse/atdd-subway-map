package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineStationService {
    public LineStation save(LineStation lineStation) {
        return lineStation;
    }

    public List<LineStationResponse> getLineStations(Long lineId) {
        List<LineStationResponse> lineStationResponses = new ArrayList<>();
        lineStationResponses.add(new LineStationResponse(1L, 1L, null, 10, 2));
        lineStationResponses.add(new LineStationResponse(1L, 2L, 1L, 10, 2));
        lineStationResponses.add(new LineStationResponse(1L, 3L, 2L, 10, 2));
        return lineStationResponses;
    }
}
