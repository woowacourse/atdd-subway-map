package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.exception.WrongIdException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
            .map(line -> LineResponse.of(line, mapLineStationsToStations(line.getId())))
            .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = getLine(id);
        persistLine.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineStationResponse addLineStation(LineStationRequest request) {
        Line line = getLine(request.getLineId());
        LineStation lineStation = request.toLineStation();
        line.addLineStation(lineStation);
        lineRepository.save(line);

        return new LineStationResponse(request.getLineId(), lineStation.getPreStationId(),
            lineStation.getStationId(), lineStation.getDistance(), lineStation.getDuration());
    }

    public void deleteLineStation(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = getLine(id);

        List<Long> stationsId = line.getStations().stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());

        return LineResponse.of(line, stationRepository.findAllById(stationsId));
    }

    private List<Station> mapLineStationsToStations(Long lineId) {
        return stationRepository.findAllOrderByKey(lineId);
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(WrongIdException::new);
    }
}
