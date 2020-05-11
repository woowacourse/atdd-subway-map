package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.LineStationRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_NAME_OVER_LAP = "존재하는 이름입니다";
    private static final String ERROR_MESSAGE_NOT_EXIST_LINE_ID = "해당 아이디의 노선이 존재하지 않습니다";

    private LineRepository lineRepository;
    private LineStationRepository lineStationRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       LineStationRepository lineStationRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    public Line save(Line line) {
        validateTitle(line);
        return lineRepository.save(line);
    }

    public void validateTitle(Line line) {
        lineRepository.findByTitle(line.getTitle())
                .ifPresent(x -> {
                    throw new IllegalArgumentException(ERROR_MESSAGE_NAME_OVER_LAP);
                });
    }

    public void updateLine(Long id, Line line) {
        validateTitleWhenUpdateInfo(id, line);
        Line persistLine = lineRepository.findById(id).orElseThrow(()->{
            throw new IllegalArgumentException(ERROR_MESSAGE_NAME_OVER_LAP);
        });
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void validateTitleWhenUpdateInfo(Long id, Line line) {
        if (isNotChangeTitle(id, line)) {
            return;
        }
        validateTitle(line);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, LineStation lineStation) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXIST_LINE_ID));
        line.updatePreStationWhenAdd(lineStation);
        line.addLineStation(lineStation);
        lineRepository.save(line);
    }

    public List<LineStation> findLineStationByLineId(Long lineId) {
        return lineStationRepository.findAllByLineId(lineId);
    }

    public LineResponse findLineWithStationsById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXIST_LINE_ID));
        List<StationResponse> stations = getStationsByLine(line);
        return LineResponse.of(line, new LinkedHashSet<>(stations));
    }

    private List<StationResponse> getStationsByLine(final Line line) {
        return line.getLineStationsId().stream()
                .map(stationId -> stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new))
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXIST_LINE_ID));
        line.updatePreStationWhenRemove(stationId);
        line.removeLineStationById(stationId);
        lineRepository.save(line);
    }

    private boolean isNotChangeTitle(final Long id, final Line line) {
        Line lineById = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        return lineById.isTitleEquals(line.getTitle());
    }
}
