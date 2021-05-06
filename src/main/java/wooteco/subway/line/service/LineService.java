package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        if (lineRequest.isSameStations()) {
            throw new IllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
        }
        Line line = lineRequestToLine(lineRequest);
        if (lineDao.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
        Line savedLine = lineDao.save(line);
        return new LineResponse(savedLine);
    }

    public Line lineRequestToLine(LineRequest lineRequest) {
        Station downStation = stationDao.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("입력하신 하행역이 존재하지 않습니다."));
        Station upStation = stationDao.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("입력하신 상행역이 존재하지 않습니다."));
        List<Station> stations = new ArrayList<>(Arrays.asList(downStation, upStation));

        return new Line(lineRequest.getName(), stations, lineRequest.getDistance(), lineRequest.getColor(),
                lineRequest.getExtraFare());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse find(Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("해당하는 노선이 존재하지 않습니다."));
        return new LineResponse(line);
    }

    public void delete(Long id) {
        lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제하려는 노선이 존재하지 않습니다"));
        lineDao.delete(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("수정하려는 노선이 존재하지 않습니다"));
        Line line = new Line(id, lineRequest.getName(), lineRequest.getExtraFare(), lineRequest.getColor());
        lineDao.update(line);
    }
}
