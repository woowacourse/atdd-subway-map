package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final JdbcSectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, JdbcSectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(final LineRequest request) {
        try {
            Line savedLine = saveLine(request);
            saveSection(savedLine.getId(), request);
            List<Station> stations = getStationsByLine(savedLine);
            return LineResponse.of(savedLine, stations);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    private Line saveLine(LineRequest request) {
        return lineDao.save(new Line(request.getName(), request.getColor()));
    }

    private void saveSection(Long lineId, LineRequest request) {
        Section section = new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        sectionDao.save(section);
    }

    private List<Station> getStationsByLine(Line line) {
        List<Section> sectionsPerLine = sectionDao.findByLineId(line.getId());
        List<Long> stationIds = new Sections(sectionsPerLine).findStationIds();
        return stationDao.findByIds(stationIds);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, getStationsByLine(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = getLineFromDao(id);
        return LineResponse.of(line, getStationsByLine(line));
    }

    private Line getLineFromDao(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_LINE_BY_ID.getContent()));
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line foundLine = getLineFromDao(id);
        final Line updateLine = new Line(request.getName(), request.getColor());
        foundLine.update(updateLine);
        lineDao.update(foundLine);
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
