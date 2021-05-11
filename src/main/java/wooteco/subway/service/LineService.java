package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.request.LineInsertRequest;
import wooteco.subway.dto.line.response.LineResponse;
import wooteco.subway.dto.station.response.StationResponse;
import wooteco.subway.exception.line.LineDuplicateException;
import wooteco.subway.exception.line.LineNotExistException;
import wooteco.subway.exception.station.StationNotExistException;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(LineInsertRequest lineInsertRequest) {
        Line line = lineInsertRequest.toLineEntity();
        validateDuplicateColorAndName(line.getColor(), line.getName());
        Line insertedLine = lineDao.insert(line);

        Section section = lineInsertRequest.toSectionEntity(insertedLine.getId());
        sectionDao.insert(section);
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
        return findStationsByLineId(line.getId())
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getSortedStationIds()
                .stream()
                .map(id -> stationDao.findById(id)
                        .orElseThrow(() -> new StationNotExistException(id)))
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
