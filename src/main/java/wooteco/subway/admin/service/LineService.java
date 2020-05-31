package wooteco.subway.admin.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineResponseCreateDto;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        validateTitle(line);
        return lineRepository.save(line);
    }

    private List<Line> showLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        List<LineResponseCreateDto> lineResponseCreateDtos = showLines().stream().
            map(line -> LineResponseCreateDto.of(line, getStations(line)))
            .collect(Collectors.toList());

        return LineResponse.listOf(lineResponseCreateDtos);
    }

    @Transactional
    public void updateLine(Long id, Line line) {
        validateTitleWhenUpdate(id, line);

        Line persistLine = findById(id);

        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addLineStation(Long id, LineStationCreateRequest request) {
        Line line = findById(id);
        LineStation lineStation = request.toLineStation();

        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findById(lineId);

        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineWithStationsById(Long id) {
        Line line = findById(id);
        return LineResponse.of(line, getStations(line));
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다"));
    }

    @Transactional(readOnly = true)
    public void validateTitle(Line requestLine) {
        lineRepository.findByTitle(requestLine.getTitle())
            .ifPresent(line -> {
                throw new IllegalArgumentException("존재하는 이름입니다");
            });
    }

    @Transactional(readOnly = true)
    public void validateTitleWhenUpdate(Long id, Line lineRequest) {
        Line line = findById(id);
        if (!line.isEqualTitle(lineRequest.getTitle())) {
            validateTitle(line);
        }
    }

    private Set<Station> getStations(Line line) {
        if (line.isStationsEmpty()) {
            return new HashSet<>();
        }
        List<Long> stationIds = line.getLineStationIds();
        return stationRepository.findAllById(stationIds);
    }
}