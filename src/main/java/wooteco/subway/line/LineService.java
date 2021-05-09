package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        long lineId = lineDao.save(line);

        return new Line(lineId, line);
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLine(long id) {
        List<Long> stationsId = lineDao.findStationsIdByLineId(id);
        List<Station> stations = stationsId.stream()
            .map(stationDao::findById)
            .collect(Collectors.toList());

        Line line = lineDao.findById(id);
        return new Line(line, stations);
    }

    public void updateLine(long id, Line line) {
        lineDao.update(id, line);
    }

    public void deleteLine(long id) {
        lineDao.delete(id);
    }
}
