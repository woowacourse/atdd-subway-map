package wooteco.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NoSuchLineException;
import wooteco.subway.exception.NoSuchStationException;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Line createLine(Line line) {
        try {
            long lineId = lineDao.save(line);
            return new Line(lineId, line);
        } catch (DataAccessException e) {
            throw new DuplicateLineException();
        }
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLine(long id) {
        long startStationId = sectionDao.findStartStationIdByLineId(id);
        long endStationId = sectionDao.findEndStationIdByLineId(id);
        Map<Long, Long> sections = sectionDao.findSectionsByLineId(id);
        List<Station> stations;

        try {
            stations = orderStations(startStationId, endStationId, sections);
        } catch (DataAccessException e) {
            throw new NoSuchStationException();
        }

        try {
            Line line = lineDao.findById(id);
            return new Line(line, stations);
        } catch (DataAccessException e) {
            throw new NoSuchLineException();
        }
    }

    private List<Station> orderStations(long startStationId, long endStationId, Map<Long, Long> sections) {
        List<Station> stations = new ArrayList<>();
        long sectionStartId = startStationId;
        stations.add(stationDao.findById(sectionStartId));

        while (sectionStartId != endStationId) {
            long sectionEndId = sections.get(sectionStartId);
            stations.add(stationDao.findById(sectionEndId));
            sectionStartId = sectionEndId;
        }

        return stations;
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
