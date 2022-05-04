package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineService {
    public LineResponse save(LineRequest lineRequest) {
        if (LineDao.existByName(lineRequest.getName())) {
            throw new IllegalArgumentException("중복된 지하철 노선 이름입니다.");
        }
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }
}
