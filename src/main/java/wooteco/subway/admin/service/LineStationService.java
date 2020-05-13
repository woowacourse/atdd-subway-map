package wooteco.subway.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.NoSuchElementException;

@Service
public class LineStationService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Autowired
    public LineStationService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineStation createLineStation(final Long lineId, final Long preStationId,
                                         final Long stationId, final int distance, final int duration) {

        final Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);

        final LineStation lineStation = new LineStation(lineId, preStationId, stationId, distance, duration);

        line.addLineStation(lineStation);
        lineRepository.save(line);

        return lineStation;
    }

    public void removeLineStation(long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

}
