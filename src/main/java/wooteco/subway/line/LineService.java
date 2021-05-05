package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public LineResponse createLine(long upStationId, long downStationId, String lineName, String lineColor) {
        List<Station> stations = new ArrayList<>();
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        stations.add(upStation);
        stations.add(downStation);

        Line line = new Line(lineName, lineColor, stations);
        Line newLine = lineDao.save(line);

        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(long id) {
        Optional<Line> validLine = lineDao.findById(id);
        if(!validLine.isPresent()){
            throw new IllegalArgumentException("노선 조회에 실패하였습니다.");
        }
        Line line = validLine.get();
        return new LineResponse(line.getId(), line.getName(), line.getColor(), generateStationResponse(line.getStations()));
    }

    public void deleteLine(long id) {
        Optional<Line> validLine = lineDao.findById(id);
        if(!validLine.isPresent()){
            throw new IllegalArgumentException("노선 삭제에 실패하였습니다.");
        }

        Line line = validLine.get();
        lineDao.delete(line);
    }

    private List<StationResponse> generateStationResponse(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
