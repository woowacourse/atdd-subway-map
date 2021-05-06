package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.model.Line;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponse.listOf(lines);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line updatedLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updatedLine);
    }

    @Transactional(readOnly = true)
    public LineResponse showLineById(Long id) {
        Line line = lineDao.findLineById(id);
        return new LineResponse(line);
    }
}
