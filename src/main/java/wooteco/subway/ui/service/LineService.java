package wooteco.subway.ui.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Line line = new Line(name, color);
        Line createdLine = lineDao.save(line);
        return new LineResponse(createdLine.getId(), name, color);
    }

    public List<LineResponse> findAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        final Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void modify(Long id, LineRequest lineRequest) {
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
