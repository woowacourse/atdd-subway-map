package wooteco.subway.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.repository.LineRepository;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class LineStationService {

    private final LineRepository lineRepository;

    @Autowired
    public LineStationService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineStation createLineStation(final Long lineId, final Long preStationId,
                                         final Long stationId, final int distance, final int duration) {
        final Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        final LineStation lineStation = new LineStation(lineId, preStationId, stationId, distance, duration);

        line.addLineStation(lineStation);
        lineRepository.save(line);

        return lineStation;
    }

    public Set<LineStation> findLineStation(long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);
        return line.getStations();
    }

    public LineStation removeLineStation(long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);
        LineStation lineStation = line.removeLineStationById(stationId);
        lineRepository.save(line);
        return lineStation;
    }

}
