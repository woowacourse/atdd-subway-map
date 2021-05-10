package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.List;
import java.util.Optional;
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
        final Optional<Line> lineWithSameName = lineDao.findLineByName(lineName);
        if (lineWithSameName.isPresent()) {
            throw new IllegalArgumentException("노선 이름이 중복됩니다.");
        }
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

    public LineResponse showLine(long lineId) {
        final Line line = findLineById(lineId);
        final List<Station> stationsInLine = findStationsInLine(lineId);
        line.setStations(stationsInLine);
        return LineResponse.from(line);
    }

    private Line findLineById(long lineId) {
        final Optional<Line> lineFoundById = lineDao.findById(lineId);
        if (!lineFoundById.isPresent()) {
            throw new IllegalArgumentException("해당 id에 대응하는 노선이 없습니다.");
        }
        return lineFoundById.get();
    }

    private List<Station> findStationsInLine(long lineId) {
        return stationDao.findStationIdsInLineByLineId(lineId)
                .stream()
                .map(stationDao::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void updateLine(long lineId, String lineName, String lineColor) {
        final Line line = findLineById(lineId);
        lineDao.update(line.getId(), lineName, lineColor);
    }

    public void deleteLine(long lineId) {
        final Line line = findLineById(lineId);
        lineDao.delete(line.getId());
    }
}
