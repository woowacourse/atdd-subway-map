package wooteco.subway.admin.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.DuplicateLineException;
import wooteco.subway.admin.exception.InvalidStationInsertionException;
import wooteco.subway.admin.exception.LineNotFoundException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        if (isDistinct(line.getName())) {
            return lineRepository.save(line);
        }
        throw new DuplicateLineException(line.getName());
    }

    private boolean isDistinct(String name) {
        return lineRepository.countDistinctByName(name) == 0;
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        persistLine.update(line);
        return lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        lineRepository.deleteById(persistLine.getId());
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line persistLine = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        if (request.getPreStationId() == null) {
            Optional<LineStation> first = persistLine.getStations().stream()
                .filter(lineStation -> lineStation.getPreStationId() == null)
                .findFirst();
            first.ifPresent(
                lineStation -> {
                    lineStation.updatePreLineStation(request.getStationId());
                });
            persistLine.addLineStation(0, request.toLineStation());
            lineRepository.save(persistLine);
            return;
        }

        boolean b = persistLine.getStations().stream()
            .noneMatch(lineStation -> lineStation.getStationId() == request.getPreStationId());

        if (b) {
            throw new InvalidStationInsertionException(request.getPreStationId());
        }

        Optional<LineStation> first = persistLine.getStations()
            .stream()
            .filter(lineStation -> request.getPreStationId().equals(lineStation.getPreStationId()))
            .findFirst();

        if (first.isPresent()) {
            int index = persistLine.getStations().indexOf(first.get());
            persistLine.addLineStation(index, request.toLineStation());
            first.get().updatePreLineStation(request.getStationId());
            lineRepository.save(persistLine);
            return;
        }

        persistLine.addLineStation(request.toLineStation());
        lineRepository.save(persistLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line persistLine = lineRepository.findById(lineId)
            .orElseThrow(() -> new LineNotFoundException(lineId));
        persistLine.removeLineStationById(stationId);
        lineRepository.save(persistLine);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id));
        Set<Station> stations = stationRepository.findAllById(line.getLineStationsId());
        return LineResponse.of(line, stations);
    }
}
