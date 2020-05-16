package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exceptions.DuplicationNameException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public List<LineResponse> findAllLines() {
        return LineResponse.listOf(lineRepository.findAll());
    }

    public LineResponse saveLine(final Line line) {
        if (lineRepository.existsByName(line.getName())) {
            throw new DuplicationNameException(line.getName());
        }
        return LineResponse.of(lineRepository.save(line));
    }

    public void updateLine(final Long id, final Line line) {
        Line persistLine = getLineWithoutStations(id);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    private Line getLineWithoutStations(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(final Long id, final LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        LineStation lineStation = new LineStation(id, request.getPreStationId(), request.getStationId(),
                request.getDistance(), request.getDuration());
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public void removeLineStation(final Long lineId, final Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse getLineWithStationsById(final Long id) {
        Line line = getLineWithoutStations(id);
        List<Station> listStations = stationRepository.findAllById(line.getLineStationsId());

        Set<StationResponse> stations = new LinkedHashSet<>(
                listStations.stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList())
        );
        return LineResponse.of(line, stations);
    }
}
