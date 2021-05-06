package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

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
        long lineId = lineDao.save(lineName, lineColor);
        sectionDao.save(lineId, upStationId, downStationId);

        return new LineResponse(lineId, lineName, lineColor);
    }

    public List<LineResponse> showLines() {
        return lineDao.findAll();
    }

    public LineResponse showLine(long id) {
        List<Long> stationsId = lineDao.findStationsIdByLineId(id);
        List<StationResponse> stations = stationsId.stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());

        LineResponse lineResponse = lineDao.findById(id);
        return new LineResponse(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor(), stations);
    }

    public void updateLine(long id, String lineName, String lineColor) {
        lineDao.update(id, lineName, lineColor);
    }

    public void deleteLine(long id) {
        lineDao.delete(id);
    }
}
