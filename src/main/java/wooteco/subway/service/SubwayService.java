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
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SubwayService {

    private LineDao lineDao;
    private StationDao stationDao;

    public SubwayService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine);
    }

    public List<LineResponse> getLines() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRequest.toEntity(id);
        lineDao.update(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRequest.toEntity();
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    public List<StationResponse> getStations() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
