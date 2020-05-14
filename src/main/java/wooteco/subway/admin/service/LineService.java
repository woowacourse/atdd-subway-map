package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.DuplicateLineException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {
    final private LineRepository lineRepository;
    final private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getTitle(), lineRequest.getStartTime(), lineRequest.getEndTime(),
                lineRequest.getIntervalTime(), lineRequest.getBgColor());
        lineRepository.findByTitle(line.getTitle())
                .ifPresent(this::throwDuplicateException);

        Line saveLine = lineRepository.save(line);
        return LineResponse.of(saveLine);
    }

    private void throwDuplicateException(Line line) {
        System.out.println("중복 발생");
        throw new DuplicateLineException(line.getTitle());
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(lineRequest.getTitle(), lineRequest.getStartTime(), lineRequest.getEndTime(),
                lineRequest.getIntervalTime(), lineRequest.getBgColor());
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return LineResponse.of(lineRepository.save(persistLine));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public List<Station> showStations() {
        return stationRepository.findAll();
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        if (request.hasNotAnyId()) {
            convertNameToId(request);
        }
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        LineStation lineStation = new LineStation(request.getPreStationId(), request.getStationId(),
                request.getDistance(), request.getDuration());

        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    private void convertNameToId(LineStationCreateRequest request) {
        if (!request.getPreStationName().isEmpty()) {
            Station preStation = stationRepository.findByName(request.getPreStationName())
                    .orElseThrow(IllegalArgumentException::new);
            request.setPreStationId(preStation.getId());
        }
        Station station = stationRepository.findByName(request.getStationName())
                .orElseThrow(IllegalArgumentException::new);
        request.setStationId(station.getId());

    }

    public List<LineResponse> findAllStationsWithLine() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(Line::getId)
                .map(this::findStationsByLineId)
                .collect(Collectors.toList());
    }

    public LineResponse findStationsByLineId(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        List<Station> stations = new ArrayList<>();

        List<LineStation> lineStations = line.getLineStations();
        for (LineStation lineStation : lineStations) {
            checkAllStationById(line, stations, lineStation);
        }
        return LineResponse.of(line, stations);
    }

    private void checkAllStationById(Line line, List<Station> stations, LineStation lineStation) {
        for (Station station : stationRepository.findAllById(line.getLineStationsId())) {
            checkSameId(stations, lineStation, station);
        }
    }

    private void checkSameId(List<Station> stations, LineStation lineStation, Station station) {
        if (station.getId() == lineStation.getStationId()) {
            stations.add(station);
        }
    }

    public void deleteStationByLineIdAndStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }
}