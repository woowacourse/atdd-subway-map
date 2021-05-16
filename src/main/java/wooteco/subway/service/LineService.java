package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.duplicate.DuplicateLineException;
import wooteco.subway.exception.nosuch.NoSuchLineException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    private final SectionService sectionService;
    private final StationService stationService;

    @Autowired
    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
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
        Sections sections = sectionService.makeStationsInLine(id);

        List<Station> stations = new ArrayList<>();
        for (Long stationId : sections.getStationIds()) {
            stations.add(stationService.showStation(stationId));
        }

        return new Line(line, stations);
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
