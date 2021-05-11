package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.line.request.LineInsertRequest;
import wooteco.subway.dto.line.response.LineResponse;
import wooteco.subway.dto.station.response.StationResponse;
import wooteco.subway.exception.line.LineDuplicateException;
import wooteco.subway.exception.line.LineNotExistException;
import wooteco.subway.repository.SubwayRepository;
import wooteco.subway.repository.dao.LineDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SubwayRepository subwayRepository;

    public LineService(LineDao lineDao, SubwayRepository subwayRepository) {
        this.lineDao = lineDao;
        this.subwayRepository = subwayRepository;
    }

    public LineResponse create(LineInsertRequest lineInsertRequest) {
        Line line = lineInsertRequest.toLineEntity();
        validateDuplicateColorAndName(line.getColor(), line.getName());
        Line insertedLine = lineDao.insert(line);

        Section section = lineInsertRequest.toSectionEntity(insertedLine.getId());
        subwayRepository.insertSectionWithLineId(section);
        List<StationResponse> stationResponses = stationsToStationResponses(insertedLine);

        return new LineResponse(insertedLine, stationResponses);
    }

    public List<LineResponse> showAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse showById(Long lineId) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new LineNotExistException(lineId));
        List<StationResponse> stationResponses = stationsToStationResponses(line);

        return new LineResponse(line, stationResponses);
    }

    private List<StationResponse> stationsToStationResponses(Line line) {
        return subwayRepository.findStationsByLineId(line.getId())
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void updateById(Long id, String color, String name) {
        validateDuplicateColorAndName(color, name);
        lineDao.update(id, color, name);
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }

    private void validateDuplicateColorAndName(String color, String name) {
        lineDao.findByColor(color)
                .ifPresent(line -> {
                    throw new LineDuplicateException(line.getColor());
                });
        lineDao.findByName(name)
                .ifPresent(line -> {
                    throw new LineDuplicateException(line.getName());
                });
    }
}
