package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public Line createLine(long upStationId, long downStationId, String lineName, String lineColor) {
        long lineId = lineDao.save(lineName, lineColor);
        sectionDao.save(lineId, upStationId, downStationId);

        return new Line(lineId, lineName, lineColor, Collections.emptyList());
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

        return new Line(id, line.getName(), line.getColor(), stations);
    }

    public void updateLine(long id, String lineName, String lineColor) {
        lineDao.update(id, lineName, lineColor);
    }

    public void deleteLine(long id) {
        lineDao.delete(id);
    }
}
