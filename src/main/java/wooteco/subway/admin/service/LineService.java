package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        if (lineRepository.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 노선 이름은 허용되지 않습니다.");
        }

        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            List<Station> stations = new ArrayList<>();
            for (Long stationId : line.getLineStationsId()) {
                stations.add(stationRepository.findById(stationId).get());
            }
            lineResponses.add(LineResponse.of(line, stations));
        }
        return lineResponses;
    }

    public LineResponse showLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
        List<Station> stations = stationRepository.findAllById(line.getLineStationsId());

        return LineResponse.of(line, stations);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
        if (lineRepository.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 노선 이름은 허용되지 않습니다.");
        }
        line.update(lineRequest.toLine());
        lineRepository.save(line);
        List<Station> stations = stationRepository.findAllById(line.getLineStationsId());
        return LineResponse.of(line, stations);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationCreateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        line.addLineStation(request.toEntity());
        lineRepository.save(line);
        List<Station> stations = new ArrayList<>();
        for (Long stationId : line.getLineStationsId()) {
            stations.add(stationRepository.findById(stationId).get());
        }
        return LineResponse.of(line, stations);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    public LineResponse findLineWithStationsById(Long stationId) {
        Line line = lineRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
        List<Station> stations = stationRepository.findAllById(line.getLineStationsId());

        return LineResponse.of(line, stations);
    }
}