package wooteco.subway.admin.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;
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
        return lineRepository.save(line);
    }

    public boolean existsByName(String name) {
        return lineRepository.existsByName(name);
    }

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public Line showLine(Long id) {
        return lineRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, LineStationCreateRequest lineStationCreateRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        LineStation lineStation = lineStationCreateRequest.toLineStation();
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }
    //
    // public LineResponse findLineWithStationsById(Long id) {
    //     // TODO: Service의 반환타입이 Line? LineReponse?
    //     return new LineResponse();
    // }

    public Set<Station> toStations(List<Long> lineStationsId) {
        Set<Station> stations = new LinkedHashSet<>();
        for (Long id : lineStationsId) {
            Station station = stationRepository.findById(id)
                .orElseThrow(RuntimeException::new);
            stations.add(station);
        }
        return stations;
    }
}
