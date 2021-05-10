package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

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

    public LineResponse createLine(long upStationId, long downStationId, String lineName, String lineColor) {
        Line line = lineDao.save(lineName, lineColor);
        sectionDao.save(line.getId(), upStationId, downStationId);
        return LineResponse.from(line);
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse showLine(long id) {
        final Line line = lineDao.findById(id);
        final List<Station> stationsInLine = stationDao.findStationsIdInLineId(id)
                .stream()
                .map(stationId -> stationDao.findById(stationId).get())
                .collect(Collectors.toList());

        line.setStations(stationsInLine);
        return LineResponse.from(line);
    }

    public void updateLine(long id, String lineName, String lineColor) {
        lineDao.update(id, lineName, lineColor);
    }

    public void deleteLine(long id) {
        lineDao.delete(id);
    }
}
