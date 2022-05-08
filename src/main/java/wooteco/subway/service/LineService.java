package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance());
        Line newLine = lineDao.save(line);
        StationResponse upStation = new StationResponse(stationDao.findById(newLine.getUpStationId()));
        StationResponse downStation = new StationResponse(stationDao.findById(newLine.getDownStationId()));
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), List.of(upStation, downStation));
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(
                        it.getId(),
                        it.getName(),
                        it.getColor(),
                        it.getStations().stream()
                            .map(StationResponse::new)
                            .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        StationResponse upStation = new StationResponse(stationDao.findById(line.getUpStationId()));
        StationResponse downStation = new StationResponse(stationDao.findById(line.getDownStationId()));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), List.of(upStation, downStation));
    }

    public void changeField(LineResponse findLine, LineRequest lineRequest) {
        lineDao.changeLineName(findLine.getId(), lineRequest.getName());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
