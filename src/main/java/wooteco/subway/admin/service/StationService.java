package wooteco.subway.admin.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public StationService(
        StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public StationResponse save(StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();
        stationRepository.save(station);
        return StationResponse.of(station);
    }

    public List<StationResponse> showStations() {
        return StationResponse.listOf(stationRepository.findAll());
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
        updateLineRemovedStation(id);
    }

    private void updateLineRemovedStation(Long id) {
        List<Line> lines = lineRepository.findLinesByStationId(id);
        for (Line line : lines) {
            line.removeLineStationById(id);
        }
        lineRepository.saveAll(lines);
    }
}
