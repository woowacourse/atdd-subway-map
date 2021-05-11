package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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

    public LineResponse createLine(long upStationId, long downStationId, String lineName, String lineColor, int distance) {
        final Optional<Line> lineWithSameName = lineDao.findByName(lineName);
        if (lineWithSameName.isPresent()) {
            throw new IllegalArgumentException("노선 이름이 중복됩니다.");
        }
        Line line = lineDao.save(lineName, lineColor);

        final Station upStation = stationDao.findById(upStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        final Station downStation = stationDao.findById(downStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        line.setStations(stations);

        sectionDao.save(line.getId(), upStationId, downStationId, distance);
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
        return lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id에 대응하는 노선이 없습니다."));
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
