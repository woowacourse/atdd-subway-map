package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
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

    public List<Line> showLines() {
        return lineRepository.findAll();
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long id, LineStationCreateRequest request) {
        // TODO: 구현
    }

    public void removeLineStation(Long lineId, Long stationId) {
        // TODO: 구현
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존지하지 않습니다"));
        return LineResponse.of(line);
    }

    public void validLine(LineRequest lineRequest) {
        //이름중복검사
        lineRepository.findByTitle(lineRequest.getTitle()).ifPresent(line -> {
            throw new IllegalArgumentException("존재하는 이름입니다");
        });
        //이름형식검사

    }

    public void deleteLineByName(String name) {
        lineRepository.findByTitle(name).ifPresent(line -> {
            lineRepository.delete(line);
        });
    }
}
