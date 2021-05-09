package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

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

    public Line createLine(Line line, Section section) {
        long lineId = lineDao.save(line);
        sectionDao.save(section);

        return new Line(lineId, line);
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLine(long id) {
        List<Long> stationsId = lineDao.findStationsIdByLineId(id);
        List<StationResponse> stations = stationsId.stream()
            .map(stationDao::findById)
            .collect(Collectors.toList());

        return lineDao.findById(id);
    }

    public void updateLine(long id, String lineName, String lineColor) {
        lineDao.update(id, lineName, lineColor);
    }

    public void deleteLine(long id) {
        lineDao.delete(id);
    }
}
