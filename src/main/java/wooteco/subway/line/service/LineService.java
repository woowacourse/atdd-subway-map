package wooteco.subway.line.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.exception.duplicate.DuplicateLineException;
import wooteco.subway.exception.nosuch.NoSuchLineException;
import wooteco.subway.line.Line;
import wooteco.subway.line.StationsInLine;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.service.SectionService;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    private final SectionService sectionService;

    @Autowired
    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public Line createLine(Line line) {
        try {
            long lineId = lineDao.save(line);
            return Line.of(lineId, line);
        } catch (DataAccessException | NullPointerException exception) {
            throw new DuplicateLineException();
        }
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLine(long id) {
        Line line = lineDao.findById(id).orElseThrow(NoSuchLineException::new);
        StationsInLine stationsInLine = sectionService.makeStationsInLine(id);
        return new Line(line, stationsInLine.getStations());
    }

    public void updateLine(long id, Line line) {
        if (lineDao.update(id, line) != 1) {
            throw new NoSuchLineException();
        }
    }

    public void deleteLine(long id) {
        if (lineDao.delete(id) != 1) {
            throw new NoSuchLineException();
        }
    }
}
