package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationAddRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private final Map<Long, Station> stationMockTable = new HashMap<>();

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(Line line) {
        validate(line);
        return LineResponse.of(lineRepository.save(line));
    }

    private void validate(Line line) {
        List<Line> savedLines = lineRepository.findAll();
        for (Line savedLine : savedLines) {
            if (savedLine.getName().equals(line.getName())) {
                throw new IllegalArgumentException("중복되는 역이 존재합니다.");
            }
        }
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        return LineResponse.of(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        return LineResponse.of(lineRepository.save(persistLine));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addLineStation(Long id, LineStationAddRequest request) {
        // TODO: 구현
        //request의 lineName을 받아서 line을 찾고,
        //station 두 개가 존재하는지 찾고 -> 없으면 에러
        //한개의 lineStation 생성
        //그걸 line에 add하고 line을 save
        Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        Station station1 = new Station(request.getPreStationName());
        Station station2 = new Station(request.getStationName());

        stationMockTable.put(1L, station1);
        stationMockTable.put(2L, station2);
        LineStation lineStation = new LineStation(1L, 2L, 10, 10);

        line.addLineStation(lineStation);

        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
    }

    public LineResponse findLineWithStationsById(Long id) {
        // TODO: 구현
        return new LineResponse();
    }
}
