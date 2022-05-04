package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineService {
    public LineResponse save(LineRequest lineRequest) {
        validateNameDuplication(lineRequest.getName());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateNameDuplication(String name) {
        if (LineDao.existByName(name)) {
            throw new IllegalArgumentException("중복된 지하철 노선 이름입니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineResponse find(Long id) {
        Line line = LineDao.find(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void update(Long id, LineRequest lineRequest) {
        if (!LineDao.existById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선 id입니다.");
        }
        validateNameDuplication(lineRequest.getName());
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        LineDao.update(id, line);
    }
}
