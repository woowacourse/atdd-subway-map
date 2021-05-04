package wooteco.subway.line.service;

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

public class LineService {

    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRequestToLine(lineRequest);
        if (LineDao.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
        Line savedLine = LineDao.save(line);
        return new LineResponse(savedLine);
    }

    public Line lineRequestToLine(LineRequest lineRequest) {
        Station downStation = StationDao.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("입력하신 하행역이 존재하지 않습니다."));
        Station upStation =
                StationDao.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException(
                        "입력하신 상행역이 존재하지 않습니다."));
        List<Station> stations = new ArrayList<>(Arrays.asList(downStation, upStation));

        return new Line(lineRequest.getName(), stations, lineRequest.getDistance(), lineRequest.getColor(),
                lineRequest.getExtraFare());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse find(Long id) {
        Line line = LineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("해당하는 노선이 존재하지 않습니다."));
        return new LineResponse(line);
    }

    public void delete(Long id) {
        LineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제하려는 노선이 존재하지 않습니다"));
        LineDao.delete(id);
    }
}
